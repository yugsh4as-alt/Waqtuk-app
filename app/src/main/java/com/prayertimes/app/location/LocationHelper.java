package com.prayertimes.app.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;

import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationHelper {

    public interface Callback {
        void onLocationReceived(double latitude, double longitude, double altitude, String cityName);
        void onLocationFailed(String reason);
    }

    private static final long TIMEOUT_MS       = 10_000;
    private static final long MAX_LOCATION_AGE = 5 * 60 * 1000L; // 5 minutes

    private final Context                    context;
    private final FusedLocationProviderClient fusedClient;
    private final Handler                    mainHandler = new Handler(Looper.getMainLooper());
    private       LocationCallback           locationCallback;
    private       boolean                    delivered   = false;

    public LocationHelper(Context context) {
        this.context     = context.getApplicationContext();
        this.fusedClient = LocationServices.getFusedLocationProviderClient(this.context);
    }

    public boolean hasPermission() {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                   == PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                   == PackageManager.PERMISSION_GRANTED;
    }

    public void fetchLocation(Callback callback) {
        if (!hasPermission()) {
            callback.onLocationFailed("no_permission");
            return;
        }
        delivered = false;
        stopUpdates();

        // Fast path: use last known if recent
        try {
            fusedClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null && !delivered) {
                        long age = System.currentTimeMillis() - location.getTime();
                        if (age < MAX_LOCATION_AGE) {
                            delivered = true;
                            mainHandler.removeCallbacksAndMessages(null);
                            stopUpdates();
                            deliverLocation(location, callback);
                            return;
                        }
                    }
                    requestFreshFix(callback);
                })
                .addOnFailureListener(e -> requestFreshFix(callback));
        } catch (SecurityException e) {
            requestFreshFix(callback);
        }
    }

    private void requestFreshFix(Callback callback) {
        if (delivered) return;

        LocationRequest req = new LocationRequest.Builder(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY, 3_000)
                .setMaxUpdates(1)
                .setWaitForAccurateLocation(false)
                .build();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult result) {
                if (delivered) return;
                Location loc = result.getLastLocation();
                if (loc != null) {
                    delivered = true;
                    mainHandler.removeCallbacksAndMessages(null);
                    stopUpdates();
                    deliverLocation(loc, callback);
                }
            }
        };

        try {
            fusedClient.requestLocationUpdates(req, locationCallback, Looper.getMainLooper());
        } catch (SecurityException e) {
            callback.onLocationFailed("permission_denied");
            return;
        }

        // Timeout fallback
        mainHandler.postDelayed(() -> {
            if (!delivered) {
                delivered = true;
                stopUpdates();
                // Last attempt: use any cached location regardless of age
                try {
                    fusedClient.getLastLocation()
                        .addOnSuccessListener(loc -> {
                            if (loc != null) deliverLocation(loc, callback);
                            else callback.onLocationFailed("timeout");
                        })
                        .addOnFailureListener(e -> callback.onLocationFailed("timeout"));
                } catch (SecurityException e) {
                    callback.onLocationFailed("timeout");
                }
            }
        }, TIMEOUT_MS);
    }

    public void stopUpdates() {
        mainHandler.removeCallbacksAndMessages(null);
        if (locationCallback != null) {
            try { fusedClient.removeLocationUpdates(locationCallback); }
            catch (Exception ignored) {}
            locationCallback = null;
        }
    }

    private void deliverLocation(Location loc, Callback callback) {
        double lat  = loc.getLatitude();
        double lon  = loc.getLongitude();
        double alt  = loc.hasAltitude() ? loc.getAltitude() : 0;
        String city = resolveCityName(lat, lon);
        callback.onLocationReceived(lat, lon, alt, city);
    }

    private String resolveCityName(double lat, double lon) {
        try {
            if (Geocoder.isPresent()) {
                Geocoder geo = new Geocoder(context, Locale.ENGLISH);
                List<Address> list = geo.getFromLocation(lat, lon, 1);
                if (list != null && !list.isEmpty()) {
                    Address a    = list.get(0);
                    String city  = a.getLocality();
                    if (city == null || city.isEmpty()) city = a.getSubAdminArea();
                    if (city == null || city.isEmpty()) city = a.getAdminArea();
                    if (city == null || city.isEmpty()) city = a.getCountryName();
                    if (city != null && !city.isEmpty()) return city;
                }
            }
        } catch (IOException | IllegalArgumentException ignored) {}
        return String.format(Locale.US, "%.3f°, %.3f°", lat, lon);
    }
}

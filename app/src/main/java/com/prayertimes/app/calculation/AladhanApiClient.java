package com.prayertimes.app.calculation;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.prayertimes.app.utils.AppPreferences;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Fetches official prayer times from aladhan.com API (free, no key needed).
 *
 * Flow:
 *   1. Called once when location is set for the first time.
 *   2. Fetches today's times from API.
 *   3. Computes difference (in minutes) between API times and local calculation.
 *   4. Saves the per-prayer adjustments to AppPreferences.
 *   5. Never called again unless location changes.
 *
 * API docs: https://aladhan.com/prayer-times-api
 */
public class AladhanApiClient {

    public interface Callback {
        void onSuccess(int[] adjustments); // per-prayer minute adjustments [7]
        void onFailure(String reason);
    }

    private static final String TAG = "AladhanApiClient";
    private static final String BASE_URL = "https://api.aladhan.com/v1/timings";

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private final Context context;

    public AladhanApiClient(Context context) {
        this.context = context.getApplicationContext();
    }

    /** Check if device has network connectivity. */
    public boolean isNetworkAvailable() {
        try {
            ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm == null) return false;
            NetworkInfo ni = cm.getActiveNetworkInfo();
            return ni != null && ni.isConnected();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Fetch prayer times from API and compute adjustments vs local calculation.
     * Runs on background thread, delivers result on main thread.
     *
     * @param lat       location latitude
     * @param lon       location longitude
     * @param prefs     AppPreferences (to read current calc method)
     * @param callback  result callback
     */
    public void fetchAndComputeAdjustments(double lat, double lon,
                                           AppPreferences prefs, Callback callback) {
        executor.execute(() -> {
            try {
                // Get today's date
                Calendar cal = Calendar.getInstance();
                int day   = cal.get(Calendar.DAY_OF_MONTH);
                int month = cal.get(Calendar.MONTH) + 1;
                int year  = cal.get(Calendar.YEAR);

                // Map our method to aladhan method number
                int aladhanMethod = mapMethod(prefs.getCalcMethod());

                // Build URL: GET /v1/timings/{DD-MM-YYYY}?latitude=...&longitude=...&method=...
                String dateStr = String.format("%02d-%02d-%04d", day, month, year);
                String urlStr  = BASE_URL + "/" + dateStr
                    + "?latitude=" + lat
                    + "&longitude=" + lon
                    + "&method=" + aladhanMethod
                    + "&tune=0,0,0,0,0,0,0,0,0"; // no adjustments

                Log.d(TAG, "Fetching: " + urlStr);

                String json = httpGet(urlStr);
                if (json == null) {
                    mainHandler.post(() -> callback.onFailure("network_error"));
                    return;
                }

                // Parse response
                JSONObject root = new JSONObject(json);
                if (!root.getString("status").equals("OK")) {
                    mainHandler.post(() -> callback.onFailure("api_error"));
                    return;
                }

                JSONObject timings = root.getJSONObject("data").getJSONObject("timings");

                // Extract API times (HH:MM format)
                double apiFajr    = parseTime(timings.getString("Fajr"));
                double apiSunrise = parseTime(timings.getString("Sunrise"));
                double apiDhuhr   = parseTime(timings.getString("Dhuhr"));
                double apiAsr     = parseTime(timings.getString("Asr"));
                double apiSunset  = parseTime(timings.getString("Sunset"));
                double apiMaghrib = parseTime(timings.getString("Maghrib"));
                double apiIsha    = parseTime(timings.getString("Isha"));

                double[] apiTimes = {
                    apiFajr, apiSunrise, apiDhuhr,
                    apiAsr, apiSunset, apiMaghrib, apiIsha
                };

                // Compute local times
                int method = prefs.getCalcMethod();
                int asr    = prefs.getAsrJuristic();
                PrayerCalculator calc = new PrayerCalculator(lat, lon, 0, method, asr);
                double[] localTimes = calc.calculateToday();

                // Compute adjustments (round to nearest minute)
                int[] adjustments = new int[7];
                for (int i = 0; i < 7; i++) {
                    double diffHours = apiTimes[i] - localTimes[i];
                    // Handle midnight crossing
                    if (diffHours >  12) diffHours -= 24;
                    if (diffHours < -12) diffHours += 24;
                    adjustments[i] = (int) Math.round(diffHours * 60);
                }

                Log.d(TAG, "Adjustments computed successfully");
                final int[] result = adjustments;
                mainHandler.post(() -> callback.onSuccess(result));

            } catch (Exception e) {
                Log.e(TAG, "Error: " + e.getMessage());
                mainHandler.post(() -> callback.onFailure(e.getMessage()));
            }
        });
    }

    /** Map our method constants to aladhan.com method numbers. */
    private int mapMethod(int method) {
        switch (method) {
            case PrayerCalculator.METHOD_MWL:     return 3;  // Muslim World League
            case PrayerCalculator.METHOD_ISNA:    return 2;  // ISNA
            case PrayerCalculator.METHOD_EGYPT:   return 5;  // Egyptian
            case PrayerCalculator.METHOD_MAKKAH:  return 4;  // Umm Al-Qura
            case PrayerCalculator.METHOD_KARACHI: return 1;  // University of Karachi
            case PrayerCalculator.METHOD_TEHRAN:  return 7;  // Tehran
            default:                              return 3;  // Default MWL
        }
    }

    /** Parse "HH:MM" or "HH:MM (timezone)" → fractional hours */
    private double parseTime(String timeStr) {
        // Remove timezone suffix like " (UTC+1)"
        String clean = timeStr.split(" ")[0].trim();
        String[] parts = clean.split(":");
        int h = Integer.parseInt(parts[0]);
        int m = Integer.parseInt(parts[1]);
        return h + m / 60.0;
    }

    /** Simple HTTP GET, returns body as String or null on error. */
    private String httpGet(String urlStr) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10_000);
            conn.setReadTimeout(10_000);
            conn.setRequestProperty("Accept", "application/json");

            int code = conn.getResponseCode();
            if (code != 200) return null;

            BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            reader.close();
            return sb.toString();

        } catch (Exception e) {
            Log.e(TAG, "HTTP error: " + e.getMessage());
            return null;
        } finally {
            if (conn != null) conn.disconnect();
        }
    }
}

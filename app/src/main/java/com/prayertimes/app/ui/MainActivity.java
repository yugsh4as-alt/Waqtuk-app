package com.prayertimes.app.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.prayertimes.app.R;
import com.prayertimes.app.ui.fragments.AiChatFragment;
import com.prayertimes.app.ui.fragments.PrayerFragment;
import com.prayertimes.app.ui.fragments.QuranFragment;
import com.prayertimes.app.utils.AppPreferences;
import com.prayertimes.app.utils.TimeUtils;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQ_LOCATION = 101;
    private static final int REQ_NOTIF    = 102;

    private AppPreferences prefs;

    private PrayerFragment prayerFragment;
    private QuranFragment  quranFragment;
    private AiChatFragment aiChatFragment;

    private final Handler  nightHandler  = new Handler(Looper.getMainLooper());
    private final Runnable nightRunnable = this::checkNightMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = new AppPreferences(this);
        applyLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prayerFragment = new PrayerFragment();
        quranFragment  = new QuranFragment();
        aiChatFragment = new AiChatFragment();

        showFragment(prayerFragment);

        BottomNavigationView nav = findViewById(R.id.bottom_nav);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_prayer)      showFragment(prayerFragment);
            else if (id == R.id.nav_quran)  showFragment(quranFragment);
            else if (id == R.id.nav_ai)     showFragment(aiChatFragment);
            return true;
        });

        requestPermissions();
        nightHandler.post(nightRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        nightHandler.removeCallbacks(nightRunnable);
    }

    private void showFragment(Fragment f) {
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragment_container, f)
            .commit();
    }

    private void applyLanguage() {
        Locale locale = "ar".equals(prefs.getLanguage()) ? new Locale("ar") : Locale.ENGLISH;
        Locale.setDefault(locale);
        Configuration cfg = new Configuration(getResources().getConfiguration());
        cfg.setLocale(locale);
        getResources().updateConfiguration(cfg, getResources().getDisplayMetrics());
    }

    private void checkNightMode() {
        double[] times = prefs.getTodayTimes();
        if (times != null && times.length > 6) {
            double now  = TimeUtils.nowHours();
            double isha = times[6];
            double fajr = times[0];
            boolean night = now >= isha || now < fajr;
            int mode = night ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
            if (AppCompatDelegate.getDefaultNightMode() != mode) {
                AppCompatDelegate.setDefaultNightMode(mode);
            }
        }
        nightHandler.postDelayed(nightRunnable, 60_000);
    }

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                             Manifest.permission.ACCESS_COARSE_LOCATION}, REQ_LOCATION);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                   != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQ_NOTIF);
        }
    }

    @Override
    public void onRequestPermissionsResult(int req, @NonNull String[] p, @NonNull int[] r) {
        super.onRequestPermissionsResult(req, p, r);
        if (req == REQ_LOCATION && prayerFragment != null) {
            prayerFragment.onPermissionResult(r.length > 0 && r[0] == PackageManager.PERMISSION_GRANTED);
        }
    }
}

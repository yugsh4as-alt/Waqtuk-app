package com.prayertimes.app.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prayertimes.app.R;
import com.prayertimes.app.calculation.AladhanApiClient;
import com.prayertimes.app.calculation.MethodDetector;
import com.prayertimes.app.calculation.PrayerCalculator;
import com.prayertimes.app.location.LocationHelper;
import com.prayertimes.app.model.Prayer;
import com.prayertimes.app.utils.AlarmScheduler;
import com.prayertimes.app.utils.AppPreferences;
import com.prayertimes.app.utils.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // ─── Request codes ────────────────────────────────────────────────────────
    private static final int REQ_LOCATION     = 101;
    private static final int REQ_NOTIF        = 102;
    private static final int REQ_SET_LOCATION = 103;
    private static final int REQ_SETTINGS     = 200;

    // ─── Prayer indices & metadata ────────────────────────────────────────────
    private static final int[] PRAYER_INDICES = {
        PrayerCalculator.IDX_FAJR,
        PrayerCalculator.IDX_SUNRISE,
        PrayerCalculator.IDX_DHUHR,
        PrayerCalculator.IDX_ASR,
        PrayerCalculator.IDX_SUNSET,
        PrayerCalculator.IDX_MAGHRIB,
        PrayerCalculator.IDX_ISHA
    };
    private static final int[] ICONS = {
        R.drawable.ic_fajr, R.drawable.ic_sunrise, R.drawable.ic_dhuhr,
        R.drawable.ic_asr,  R.drawable.ic_sunset,  R.drawable.ic_maghrib, R.drawable.ic_isha
    };
    // true = this prayer has Adhan + Iqama
    private static final boolean[] IS_ADHAN = {true, false, true, true, false, true, true};

    // ─── Views ────────────────────────────────────────────────────────────────
    private TextView     tvCity, tvDate, tvHijri, tvNextPrayer, tvCountdown;
    private TextView     tvNextLabel, tvJumuahTime, tvHeaderPrayer, tvHeaderAdhan;
    private View         cardJumuah, progressBar, layoutContent;
    private RecyclerView rvPrayers;
    private PrayerAdapter adapter;

    // ─── State ────────────────────────────────────────────────────────────────
    private AppPreferences prefs;
    private LocationHelper locationHelper;
    private AladhanApiClient apiClient;
    private double[]       todayTimes;

    private final Handler  clockHandler = new Handler(Looper.getMainLooper());
    private final Runnable clockTick    = this::updateCountdown;

    // ═════════════════════════════════════════════════════════════════════════
    // Lifecycle
    // ═════════════════════════════════════════════════════════════════════════

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = new AppPreferences(this);
        applyLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationHelper = new LocationHelper(this);
        apiClient      = new AladhanApiClient(this);

        bindViews();
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        setupRecyclerView();
        showDate();
        updateHeaders();

        requestNotificationPermission();

        // Show cached times immediately, then try GPS
        if (prefs.isLocationSet()) {
            refreshPrayerTimes(
                prefs.getLatitude(), prefs.getLongitude(),
                prefs.getElevation(), prefs.getLocationName()
            );
        }

        // Always attempt GPS in background (updates silently if succeeds)
        startGpsInBackground();

        findViewById(R.id.btn_set_location).setOnClickListener(v -> showLocationDialog());
    }

    @Override
    protected void onResume() {
        super.onResume();
        clockHandler.post(clockTick);
        if (todayTimes != null) refreshDisplay();
    }

    @Override
    protected void onPause() {
        super.onPause();
        clockHandler.removeCallbacks(clockTick);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationHelper.stopUpdates();
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Language
    // ═════════════════════════════════════════════════════════════════════════

    private void applyLanguage() {
        String lang = prefs.getLanguage(); // default is "ar"
        Locale locale = "ar".equals(lang) ? new Locale("ar") : Locale.ENGLISH;
        Locale.setDefault(locale);
        Configuration cfg = new Configuration(getResources().getConfiguration());
        cfg.setLocale(locale);
        getResources().updateConfiguration(cfg, getResources().getDisplayMetrics());
    }

    private boolean isAr() {
        return "ar".equals(prefs.getLanguage());
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Menu
    // ═════════════════════════════════════════════════════════════════════════

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_set_location) {
            showLocationDialog();
        } else if (id == R.id.menu_refresh_location) {
            fetchLocationExplicit();
        } else if (id == R.id.menu_settings) {
            startActivityForResult(new Intent(this, SettingsActivity.class), REQ_SETTINGS);
        } else if (id == R.id.menu_language) {
            showLanguageDialog();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQ_SET_LOCATION || requestCode == REQ_SETTINGS)
                && resultCode == RESULT_OK) {
            updateHeaders();
            refreshPrayerTimes(
                prefs.getLatitude(), prefs.getLongitude(),
                prefs.getElevation(), prefs.getLocationName()
            );
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Location
    // ═════════════════════════════════════════════════════════════════════════

    /** Silent GPS update in background — won't show loading or dialog if fails. */
    private void startGpsInBackground() {
        if (!locationHelper.hasPermission()) {
            // Request permission only if we have no saved location yet
            if (!prefs.isLocationSet()) {
                ActivityCompat.requestPermissions(this,
                    new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    }, REQ_LOCATION);
            }
            return;
        }
        locationHelper.fetchLocation(new LocationHelper.Callback() {
            @Override
            public void onLocationReceived(double lat, double lon, double alt, String city) {
                prefs.saveLocation(lat, lon, alt, city);
                refreshPrayerTimes(lat, lon, alt, city);
            }
            @Override
            public void onLocationFailed(String reason) {
                // Silent — cached location already shown
            }
        });
    }

    /** Explicit GPS request triggered by user — shows loading and fallback dialog. */
    private void fetchLocationExplicit() {
        if (!locationHelper.hasPermission()) {
            ActivityCompat.requestPermissions(this,
                new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                }, REQ_LOCATION);
            return;
        }
        showLoading(true);
        tvCity.setText(isAr() ? "جاري تحديد الموقع..." : "جاري تحديد الموقع...");

        locationHelper.fetchLocation(new LocationHelper.Callback() {
            @Override
            public void onLocationReceived(double lat, double lon, double alt, String city) {
                prefs.saveLocation(lat, lon, alt, city);
                refreshPrayerTimes(lat, lon, alt, city);
            }
            @Override
            public void onLocationFailed(String reason) {
                showLoading(false);
                if (prefs.isLocationSet()) {
                    refreshPrayerTimes(
                        prefs.getLatitude(), prefs.getLongitude(),
                        prefs.getElevation(), prefs.getLocationName()
                    );
                    String msg = isAr()
                        ? "تعذر تحديث الموقع، يستخدم الموقع المحفوظ"
                        : "تعذر تحديث الموقع، يستخدم الموقع المحفوظ";
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                } else {
                    String msg = isAr()
                        ? "تعذر تحديد الموقع. اختر مدينة يدوياً."
                        : "تعذر تحديد الموقع. حدده يدوياً.";
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
                    showLocationDialog();
                }
            }
        });
    }

    private void showLocationDialog() {
        boolean ar = isAr();
        String[] opts = ar
            ? new String[]{"GPS تلقائي", "بحث عن مدينة", "إدخال إحداثيات"}
            : new String[]{"GPS (Auto)", "Search City", "Enter Coordinates"};

        new AlertDialog.Builder(this)
            .setTitle(ar ? "تحديد الموقع" : "Set Location")
            .setItems(opts, (d, which) -> {
                if (which == 0) fetchLocationExplicit();
                else if (which == 1) startActivityForResult(
                    new Intent(this, LocationSearchActivity.class), REQ_SET_LOCATION);
                else startActivityForResult(
                    new Intent(this, ManualCoordinatesActivity.class), REQ_SET_LOCATION);
            })
            .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
            @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission just granted — fetch location
                if (prefs.isLocationSet()) {
                    startGpsInBackground();
                } else {
                    fetchLocationExplicit();
                }
            } else {
                // Denied — if no saved location show manual dialog
                if (!prefs.isLocationSet()) {
                    showLocationDialog();
                }
            }
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Prayer times
    // ═════════════════════════════════════════════════════════════════════════

    private void refreshPrayerTimes(double lat, double lon, double alt, String city) {
        // Auto-detect best method for this location
        int method = MethodDetector.detect(lat, lon);
        int asr    = MethodDetector.detectAsr(lat, lon);
        prefs.setCalcMethod(method);
        prefs.setAsrJuristic(asr);

        refreshPrayerTimesLocal(lat, lon, alt, city);

        // Calibrate with API if needed (once per location, needs internet)
        if (prefs.needsRecalibration(lat, lon) && apiClient.isNetworkAvailable()) {
            calibrateWithApi(lat, lon, alt, city);
        }
    }

    /** Refresh using saved adjustments — pure offline. */
    private void refreshPrayerTimesLocal(double lat, double lon, double alt, String city) {
        int method = prefs.getCalcMethod();
        int asr    = prefs.getAsrJuristic();
        PrayerCalculator calc = new PrayerCalculator(lat, lon, alt, method, asr);
        double[] rawTimes = calc.calculateToday();
        todayTimes = new double[rawTimes.length];
        for (int i = 0; i < rawTimes.length; i++) {
            todayTimes[i] = rawTimes[i] + prefs.getTimeAdj(i) / 60.0;
        }
        AlarmScheduler.scheduleAll(this, todayTimes, prefs);
        tvCity.setText("\uD83D\uDCCD " + city);
        showLoading(false);
        refreshDisplay();
    }

    private void calibrateWithApi(double lat, double lon, double alt, String city) {
        tvCity.setText("\uD83D\uDCCD " + city + " \uD83D\uDD04");
        apiClient.fetchAndComputeAdjustments(lat, lon, prefs,
            new AladhanApiClient.Callback() {
                @Override
                public void onSuccess(int[] adjustments) {
                    for (int i = 0; i < adjustments.length; i++) {
                        prefs.setTimeAdj(i, adjustments[i]);
                    }
                    prefs.setApiCalibrated(true, lat, lon);
                    runOnUiThread(() -> refreshPrayerTimesLocal(lat, lon, alt, city));
                }
                @Override
                public void onFailure(String reason) {
                    runOnUiThread(() -> tvCity.setText("\uD83D\uDCCD " + city));
                }
            });
    }


    private void refreshDisplay() {
        if (todayTimes == null) return;
        boolean use24h = prefs.isUse24h();
        boolean ar     = isAr();
        String[] names = getPrayerNames(ar);

        List<Prayer> list = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            String adhanFmt = use24h
                ? PrayerCalculator.formatTime24(todayTimes[i])
                : PrayerCalculator.formatTime12(todayTimes[i]);

            String iqamaFmt = null;
            if (IS_ADHAN[i]) {
                int offset = prefs.getIqamaOffset(PRAYER_INDICES[i]);
                double iqamaH = todayTimes[i] + offset / 60.0;
                String iqamaT = use24h
                    ? PrayerCalculator.formatTime24(iqamaH)
                    : PrayerCalculator.formatTime12(iqamaH);
                iqamaFmt = (ar ? "إقامة " : "Iqama ") + iqamaT;
            }

            list.add(new Prayer(names[i], todayTimes[i], adhanFmt, iqamaFmt,
                                IS_ADHAN[i], ICONS[i]));
        }

        int nextIdx = TimeUtils.nextPrayerIndex(todayTimes);
        adapter.update(list, nextIdx, todayTimes);
        updateCountdown();
        updateJumuahCard(use24h, ar);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Jumuah card
    // ═════════════════════════════════════════════════════════════════════════

    private void updateJumuahCard(boolean use24h, boolean ar) {
        Calendar cal      = Calendar.getInstance();
        boolean isFriday  = cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY;

        if (isFriday && todayTimes != null) {
            cardJumuah.setVisibility(View.VISIBLE);
            String customTime = prefs.getJumuahTime();
            if (customTime == null || customTime.isEmpty()) {
                // Default: Dhuhr time
                String t = use24h
                    ? PrayerCalculator.formatTime24(todayTimes[PrayerCalculator.IDX_DHUHR])
                    : PrayerCalculator.formatTime12(todayTimes[PrayerCalculator.IDX_DHUHR]);
                tvJumuahTime.setText(t);
            } else {
                tvJumuahTime.setText(customTime);
            }
        } else {
            cardJumuah.setVisibility(View.GONE);
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Countdown (next prayer)
    // ═════════════════════════════════════════════════════════════════════════

    private void updateCountdown() {
        if (todayTimes == null) return;
        boolean ar     = isAr();
        double  now    = TimeUtils.nowHours();
        int     nextI  = TimeUtils.nextPrayerIndex(todayTimes);
        double  nextT  = todayTimes[nextI];
        String[] names = getPrayerNames(ar);

        tvNextPrayer.setText(names[nextI]);
        tvCountdown.setText(TimeUtils.countdown(now, nextT));

        clockHandler.removeCallbacks(clockTick);
        clockHandler.postDelayed(clockTick, 30_000);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Language dialog
    // ═════════════════════════════════════════════════════════════════════════

    private void showLanguageDialog() {
        String[] langs = {"English", "\u0627\u0644\u0639\u0631\u0628\u064a\u0629"}; // العربية
        new AlertDialog.Builder(this)
            .setTitle("اللغة / Language")
            .setItems(langs, (d, which) -> {
                prefs.setLanguage(which == 1 ? "ar" : "en");
                recreate();
            })
            .show();
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Helpers
    // ═════════════════════════════════════════════════════════════════════════

    private String[] getPrayerNames(boolean ar) {
        if (ar) return new String[]{
            "\u0627\u0644\u0641\u062c\u0631",    // الفجر
            "\u0627\u0644\u0634\u0631\u0648\u0642", // الشروق
            "\u0627\u0644\u0638\u0647\u0631",    // الظهر
            "\u0627\u0644\u0639\u0635\u0631",    // العصر
            "\u0627\u0644\u063a\u0631\u0648\u0628", // الغروب
            "\u0627\u0644\u0645\u063a\u0631\u0628", // المغرب
            "\u0627\u0644\u0639\u0634\u0627\u0621"  // العشاء
        };
        return new String[]{"Fajr", "Sunrise", "Dhuhr", "Asr", "Sunset", "Maghrib", "Isha"};
    }

    private void updateHeaders() {
        boolean ar = isAr();
        tvNextLabel.setText(ar ? "\u0627\u0644\u0635\u0644\u0627\u0629 \u0627\u0644\u0642\u0627\u062f\u0645\u0629" : "الصلاة القادمة");
        tvHeaderPrayer.setText(ar ? "\u0627\u0644\u0635\u0644\u0627\u0629" : "الصلاة");
        tvHeaderAdhan.setText(ar ? "\u0627\u0644\u0623\u0630\u0627\u0646  |  \u0627\u0644\u0625\u0642\u0627\u0645\u0629" : "Adhan  |  Iqama");
    }

    private void showDate() {
        boolean ar = isAr();
        Locale locale = ar ? new Locale("ar") : Locale.ENGLISH;
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy", locale);
        tvDate.setText(sdf.format(new Date()));
        tvHijri.setText(getHijriDate(ar));
    }

    private String getHijriDate(boolean ar) {
        Calendar cal = Calendar.getInstance();
        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH) + 1;
        int d = cal.get(Calendar.DAY_OF_MONTH);

        long a   = (14 - m) / 12;
        long yr  = y + 4800 - a;
        long mn  = m + 12 * a - 3;
        long jdn = d + (153 * mn + 2) / 5 + 365 * yr + yr / 4 - yr / 100 + yr / 400 - 32045;

        long l = jdn - 1948440 + 10632;
        long n = (l - 1) / 10631;
        l = l - 10631 * n + 354;
        long j = ((10985 - l) / 5316) * ((50 * l) / 17719)
               + (l / 5670) * ((43 * l) / 15238);
        l = l - ((30 - j) / 15) * ((17719 * j) / 50)
              - (j / 16) * ((15238 * j) / 43) + 29;
        long hMonth = (24 * l) / 709;
        long hDay   = l - (709 * hMonth) / 24;
        long hYear  = 30 * n + j - 30;

        String[] enM = {"Muharram","Safar","Rabi al-Awwal","Rabi al-Thani",
                        "Jumada al-Awwal","Jumada al-Thani","Rajab","Shaban",
                        "Ramadan","Shawwal","Dhul Qidah","Dhul Hijjah"};
        String[] arM = {"\u0645\u062d\u0631\u0645","\u0635\u0641\u0631",
                        "\u0631\u0628\u064a\u0639 \u0627\u0644\u0623\u0648\u0644",
                        "\u0631\u0628\u064a\u0639 \u0627\u0644\u062b\u0627\u0646\u064a",
                        "\u062c\u0645\u0627\u062f\u0649 \u0627\u0644\u0623\u0648\u0644\u0649",
                        "\u062c\u0645\u0627\u062f\u0649 \u0627\u0644\u062b\u0627\u0646\u064a\u0629",
                        "\u0631\u062c\u0628","\u0634\u0639\u0628\u0627\u0646",
                        "\u0631\u0645\u0636\u0627\u0646","\u0634\u0648\u0627\u0644",
                        "\u0630\u0648 \u0627\u0644\u0642\u0639\u062f\u0629",
                        "\u0630\u0648 \u0627\u0644\u062d\u062c\u0629"};

        int mi = (int) Math.max(0, Math.min(11, hMonth - 1));
        return ar
            ? hDay + " " + arM[mi] + " " + hYear + " \u0647\u0640"
            : hDay + " " + enM[mi] + " " + hYear + " AH";
    }

    private void bindViews() {
        tvCity         = findViewById(R.id.tv_city);
        tvDate         = findViewById(R.id.tv_date);
        tvHijri        = findViewById(R.id.tv_hijri);
        tvNextPrayer   = findViewById(R.id.tv_next_prayer);
        tvNextLabel    = findViewById(R.id.tv_next_label);
        tvCountdown    = findViewById(R.id.tv_countdown);
        tvJumuahTime   = findViewById(R.id.tv_jumuah_time);
        tvHeaderPrayer = findViewById(R.id.tv_header_prayer);
        tvHeaderAdhan  = findViewById(R.id.tv_header_adhan);
        cardJumuah     = findViewById(R.id.card_jumuah);
        rvPrayers      = findViewById(R.id.rv_prayers);
        progressBar    = findViewById(R.id.progress_bar);
        layoutContent  = findViewById(R.id.layout_content);
    }

    private void setupRecyclerView() {
        adapter = new PrayerAdapter(new ArrayList<>());
        rvPrayers.setLayoutManager(new LinearLayoutManager(this));
        rvPrayers.setAdapter(adapter);
        rvPrayers.setHasFixedSize(false);
    }

    private void showLoading(boolean loading) {
        if (progressBar == null || layoutContent == null) return;
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        layoutContent.setVisibility(loading ? View.GONE : View.VISIBLE);
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQ_NOTIF);
            }
        }
    }
}

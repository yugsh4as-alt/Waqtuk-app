package com.prayertimes.app.ui.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prayertimes.app.R;
import com.prayertimes.app.calculation.AladhanApiClient;
import com.prayertimes.app.calculation.MethodDetector;
import com.prayertimes.app.calculation.PrayerCalculator;
import com.prayertimes.app.location.LocationHelper;
import com.prayertimes.app.model.Prayer;
import com.prayertimes.app.ui.LocationSearchActivity;
import com.prayertimes.app.ui.ManualCoordinatesActivity;
import com.prayertimes.app.ui.PrayerAdapter;
import com.prayertimes.app.ui.SettingsActivity;
import com.prayertimes.app.utils.AlarmScheduler;
import com.prayertimes.app.utils.IslamicReminderScheduler;
import com.prayertimes.app.utils.AppPreferences;
import com.prayertimes.app.utils.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PrayerFragment extends Fragment {

    private static final int REQ_SET_LOCATION = 103;
    private static final int REQ_SETTINGS     = 200;

    private static final int[] PRAYER_INDICES = {
        PrayerCalculator.IDX_FAJR, PrayerCalculator.IDX_SUNRISE,
        PrayerCalculator.IDX_DHUHR, PrayerCalculator.IDX_ASR,
        PrayerCalculator.IDX_SUNSET, PrayerCalculator.IDX_MAGHRIB,
        PrayerCalculator.IDX_ISHA
    };
    private static final boolean[] IS_ADHAN = {true,false,true,true,false,true,true};

    private AppPreferences   prefs;
    private LocationHelper   locationHelper;
    private AladhanApiClient apiClient;
    private PrayerAdapter    adapter;
    private double[]         todayTimes;

    private TextView tvCity, tvDate, tvHijri, tvNextLabel, tvNextPrayer, tvCountdown;
    private TextView tvHeaderPrayer, tvHeaderAdhan;
    private View     cardJumuah, progressBar, layoutContent;
    private TextView tvJumuahTime;
    private RecyclerView rvPrayers;

    private final Handler  clockHandler = new Handler(Looper.getMainLooper());
    private final Runnable clockTick    = this::updateCountdown;

    @Override
    public void onCreate(@Nullable Bundle b) {
        super.onCreate(b);
        setHasOptionsMenu(true);
        prefs          = new AppPreferences(requireContext());
        locationHelper = new LocationHelper(requireContext());
        apiClient      = new AladhanApiClient(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup c, @Nullable Bundle b) {
        return inf.inflate(R.layout.fragment_prayer, c, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle b) {
        bindViews(v);
        adapter = new PrayerAdapter(new ArrayList<>());
        rvPrayers.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvPrayers.setAdapter(adapter);

        showDate();
        updateHeaders();

        v.findViewById(R.id.btn_set_location).setOnClickListener(x -> showLocationDialog());
        v.findViewById(R.id.btn_menu_settings).setOnClickListener(x ->
            startActivityForResult(new Intent(requireContext(), SettingsActivity.class), REQ_SETTINGS));

        if (prefs.isLocationSet()) {
            refreshPrayerTimes(prefs.getLatitude(), prefs.getLongitude(),
                               prefs.getElevation(), prefs.getLocationName());
        }
        startGps();
    }

    @Override
    public void onResume() {
        super.onResume();
        clockHandler.post(clockTick);
        if (todayTimes != null) refreshDisplay();
    }

    @Override
    public void onPause() {
        super.onPause();
        clockHandler.removeCallbacks(clockTick);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        locationHelper.stopUpdates();
    }

    public void onPermissionResult(boolean granted) {
        if (granted) startGps();
        else if (!prefs.isLocationSet()) showLocationDialog();
    }

    private void startGps() {
        if (!locationHelper.hasPermission()) return;
        locationHelper.fetchLocation(new LocationHelper.Callback() {
            @Override
            public void onLocationReceived(double lat, double lon, double alt, String city) {
                prefs.saveLocation(lat, lon, alt, city);
                refreshPrayerTimes(lat, lon, alt, city);
            }
            @Override
            public void onLocationFailed(String r) {
                if (!prefs.isLocationSet()) showLocationDialog();
            }
        });
    }

    private void showLocationDialog() {
        new AlertDialog.Builder(requireContext())
            .setTitle("تحديد الموقع")
            .setItems(new String[]{"GPS تلقائي", "بحث عن مدينة", "إدخال إحداثيات"}, (d, w) -> {
                if (w == 0) startGps();
                else if (w == 1) startActivityForResult(
                    new Intent(requireContext(), LocationSearchActivity.class), REQ_SET_LOCATION);
                else startActivityForResult(
                    new Intent(requireContext(), ManualCoordinatesActivity.class), REQ_SET_LOCATION);
            }).show();
    }

    @Override
    public void onActivityResult(int req, int res, Intent data) {
        super.onActivityResult(req, res, data);
        if (res == android.app.Activity.RESULT_OK &&
            (req == REQ_SET_LOCATION || req == REQ_SETTINGS)) {
            refreshPrayerTimes(prefs.getLatitude(), prefs.getLongitude(),
                               prefs.getElevation(), prefs.getLocationName());
        }
    }

    private void refreshPrayerTimes(double lat, double lon, double alt, String city) {
        int method = MethodDetector.detect(lat, lon);
        int asr    = MethodDetector.detectAsr(lat, lon);
        prefs.setCalcMethod(method);
        prefs.setAsrJuristic(asr);

        PrayerCalculator calc = new PrayerCalculator(lat, lon, alt, method, asr);
        double[] raw = calc.calculateToday();
        todayTimes = new double[raw.length];
        for (int i = 0; i < raw.length; i++) {
            todayTimes[i] = raw[i] + prefs.getTimeAdj(i) / 60.0;
        }
        prefs.saveTodayTimes(todayTimes);
        AlarmScheduler.scheduleAll(requireContext(), todayTimes, prefs);
        IslamicReminderScheduler.scheduleAll(requireContext(), todayTimes);

        if (tvCity != null) tvCity.setText(city);
        showLoading(false);
        refreshDisplay();

        if (prefs.needsRecalibration(lat, lon) && apiClient.isNetworkAvailable()) {
            calibrate(lat, lon, alt, city);
        }
    }

    private void calibrate(double lat, double lon, double alt, String city) {
        apiClient.fetchAndComputeAdjustments(lat, lon, prefs, new AladhanApiClient.Callback() {
            @Override
            public void onSuccess(int[] adj) {
                for (int i = 0; i < adj.length; i++) prefs.setTimeAdj(i, adj[i]);
                prefs.setApiCalibrated(true, lat, lon);
                if (isAdded()) requireActivity().runOnUiThread(() ->
                    refreshPrayerTimes(lat, lon, alt, city));
            }
            @Override
            public void onFailure(String r) {}
        });
    }

    private void refreshDisplay() {
        if (todayTimes == null || !isAdded()) return;
        boolean use24 = prefs.isUse24h();
        boolean ar    = isAr();
        String[] names = prayerNames(ar);

        List<Prayer> list = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            String adhanFmt = use24
                ? PrayerCalculator.formatTime24(todayTimes[i])
                : PrayerCalculator.formatTime12(todayTimes[i]);
            String iqamaFmt = null;
            if (IS_ADHAN[i]) {
                int off = prefs.getIqamaOffset(PRAYER_INDICES[i]);
                double iqH = todayTimes[i] + off / 60.0;
                String iqT = use24 ? PrayerCalculator.formatTime24(iqH) : PrayerCalculator.formatTime12(iqH);
                iqamaFmt = (ar ? "إقامة " : "Iqama ") + iqT;
            }
            list.add(new Prayer(names[i], todayTimes[i], adhanFmt, iqamaFmt, IS_ADHAN[i], R.drawable.ic_mosque));
        }
        int nextIdx = TimeUtils.nextPrayerIndex(todayTimes);
        adapter.update(list, nextIdx);
        updateCountdown();
        updateJumuah(use24, ar);
    }

    private void updateCountdown() {
        if (todayTimes == null || !isAdded()) return;
        boolean ar = isAr();
        int nextI  = TimeUtils.nextPrayerIndex(todayTimes);
        tvNextPrayer.setText(prayerNames(ar)[nextI]);
        tvCountdown.setText(TimeUtils.countdown(TimeUtils.nowHours(), todayTimes[nextI]));
        clockHandler.removeCallbacks(clockTick);
        clockHandler.postDelayed(clockTick, 30_000);
    }

    private void updateJumuah(boolean use24, boolean ar) {
        if (cardJumuah == null) return;
        boolean friday = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY;
        if (friday && todayTimes != null) {
            cardJumuah.setVisibility(View.VISIBLE);
            String custom = prefs.getJumuahTime();
            if (custom == null || custom.isEmpty()) {
                tvJumuahTime.setText(use24
                    ? PrayerCalculator.formatTime24(todayTimes[PrayerCalculator.IDX_DHUHR])
                    : PrayerCalculator.formatTime12(todayTimes[PrayerCalculator.IDX_DHUHR]));
            } else {
                tvJumuahTime.setText(custom);
            }
        } else {
            cardJumuah.setVisibility(View.GONE);
        }
    }

    private void showDate() {
        boolean ar = isAr();
        Locale locale = ar ? new Locale("ar") : Locale.ENGLISH;
        tvDate.setText(new SimpleDateFormat("EEEE، dd MMMM yyyy", locale).format(new Date()));
        tvHijri.setText(hijriDate(ar));
    }

    private String hijriDate(boolean ar) {
        Calendar cal = Calendar.getInstance();
        int y = cal.get(Calendar.YEAR), m = cal.get(Calendar.MONTH)+1, d = cal.get(Calendar.DAY_OF_MONTH);
        long a = (14-m)/12, yr = y+4800-a, mn = m+12*a-3;
        long jdn = d+(153*mn+2)/5+365*yr+yr/4-yr/100+yr/400-32045;
        long l = jdn-1948440+10632, n = (l-1)/10631;
        l = l-10631*n+354;
        long j = ((10985-l)/5316)*((50*l)/17719)+(l/5670)*((43*l)/15238);
        l = l-((30-j)/15)*((17719*j)/50)-(j/16)*((15238*j)/43)+29;
        long hm = (24*l)/709, hd = l-(709*hm)/24, hy = 30*n+j-30;
        String[] enM = {"محرم","صفر","ربيع الأول","ربيع الثاني","جمادى الأولى","جمادى الثانية","رجب","شعبان","رمضان","شوال","ذو القعدة","ذو الحجة"};
        int mi = (int) Math.max(0, Math.min(11, hm-1));
        return hd + " " + enM[mi] + " " + hy + " هـ";
    }

    private String[] prayerNames(boolean ar) {
        if (ar) return new String[]{"الفجر","الشروق","الظهر","العصر","الغروب","المغرب","العشاء"};
        return new String[]{"Fajr","Sunrise","Dhuhr","Asr","Sunset","Maghrib","Isha"};
    }

    private void updateHeaders() {
        boolean ar = isAr();
        if (tvNextLabel    != null) tvNextLabel.setText(ar ? "الصلاة القادمة" : "Next Prayer");
        if (tvHeaderPrayer != null) tvHeaderPrayer.setText(ar ? "الصلاة" : "Prayer");
        if (tvHeaderAdhan  != null) tvHeaderAdhan.setText(ar ? "الأذان  |  الإقامة" : "Adhan | Iqama");
    }

    private void showLoading(boolean on) {
        if (progressBar  != null) progressBar.setVisibility(on ? View.VISIBLE : View.GONE);
        if (layoutContent != null) layoutContent.setVisibility(on ? View.GONE : View.VISIBLE);
    }

    private boolean isAr() {
        return "ar".equals(prefs.getLanguage());
    }

    private void bindViews(View v) {
        tvCity         = v.findViewById(R.id.tv_city);
        tvDate         = v.findViewById(R.id.tv_date);
        tvHijri        = v.findViewById(R.id.tv_hijri);
        tvNextLabel    = v.findViewById(R.id.tv_next_label);
        tvNextPrayer   = v.findViewById(R.id.tv_next_prayer);
        tvCountdown    = v.findViewById(R.id.tv_countdown);
        tvJumuahTime   = v.findViewById(R.id.tv_jumuah_time);
        tvHeaderPrayer = v.findViewById(R.id.tv_header_prayer);
        tvHeaderAdhan  = v.findViewById(R.id.tv_header_adhan);
        cardJumuah     = v.findViewById(R.id.card_jumuah);
        rvPrayers      = v.findViewById(R.id.rv_prayers);
        progressBar    = v.findViewById(R.id.progress_bar);
        layoutContent  = v.findViewById(R.id.layout_content);
    }
}

package com.prayertimes.app.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.prayertimes.app.R;
import com.prayertimes.app.calculation.PrayerCalculator;
import com.prayertimes.app.utils.AppPreferences;

public class SettingsActivity extends AppCompatActivity {

    private AppPreferences prefs;

    // Adjustment value views
    private TextView tvAdjFajr, tvAdjDhuhr, tvAdjAsr, tvAdjMaghrib, tvAdjIsha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("الإعدادات");
        }

        prefs = new AppPreferences(this);

        setupCalcMethodSpinner();
        setupAsrSpinner();
        setupAdjustments();
        setupIqamaFields();
        setupJumuahField();
        setup24hSwitch();
        setupAdhanSwitches();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            setResult(RESULT_OK);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    // ─── Calc Method ─────────────────────────────────────────────────────────

    private void setupCalcMethodSpinner() {
        Spinner spinner = findViewById(R.id.spinner_calc_method);
        String[] methods = {
            "رابطة العالم الإسلامي",
            "ISNA - أمريكا الشمالية",
            "الهيئة المصرية العامة للمساحة",
            "أم القرى - مكة المكرمة",
            "جامعة كراتشي",
            "معهد طهران",
            "الجعفري / ليفا"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_item, methods);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(prefs.getCalcMethod());
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> p, android.view.View v, int pos, long id) {
                prefs.setCalcMethod(pos);
                // Reset calibration so it runs again with new method
                prefs.setApiCalibrated(false, 0, 0);
            }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        });
    }

    private void setupAsrSpinner() {
        Spinner spinner = findViewById(R.id.spinner_asr);
        String[] methods = {"شافعي / مالكي / حنبلي", "حنفي"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_item, methods);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(prefs.getAsrJuristic());
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> p, android.view.View v, int pos, long id) {
                prefs.setAsrJuristic(pos);
            }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        });
    }

    // ─── Time adjustments ────────────────────────────────────────────────────

    private void setupAdjustments() {
        tvAdjFajr    = findViewById(R.id.tv_adj_fajr);
        tvAdjDhuhr   = findViewById(R.id.tv_adj_dhuhr);
        tvAdjAsr     = findViewById(R.id.tv_adj_asr);
        tvAdjMaghrib = findViewById(R.id.tv_adj_maghrib);
        tvAdjIsha    = findViewById(R.id.tv_adj_isha);

        updateAdjViews();

        bindAdj(R.id.btn_adj_fajr_plus,    R.id.btn_adj_fajr_minus,    PrayerCalculator.IDX_FAJR,    tvAdjFajr);
        bindAdj(R.id.btn_adj_dhuhr_plus,   R.id.btn_adj_dhuhr_minus,   PrayerCalculator.IDX_DHUHR,   tvAdjDhuhr);
        bindAdj(R.id.btn_adj_asr_plus,     R.id.btn_adj_asr_minus,     PrayerCalculator.IDX_ASR,     tvAdjAsr);
        bindAdj(R.id.btn_adj_maghrib_plus, R.id.btn_adj_maghrib_minus, PrayerCalculator.IDX_MAGHRIB, tvAdjMaghrib);
        bindAdj(R.id.btn_adj_isha_plus,    R.id.btn_adj_isha_minus,    PrayerCalculator.IDX_ISHA,    tvAdjIsha);
    }

    private void bindAdj(int plusId, int minusId, int prayerIdx, TextView display) {
        findViewById(plusId).setOnClickListener(v -> {
            int current = prefs.getTimeAdj(prayerIdx);
            prefs.setTimeAdj(prayerIdx, current + 1);
            updateAdjViews();
        });
        findViewById(minusId).setOnClickListener(v -> {
            int current = prefs.getTimeAdj(prayerIdx);
            prefs.setTimeAdj(prayerIdx, current - 1);
            updateAdjViews();
        });
    }

    private void updateAdjViews() {
        tvAdjFajr.setText(formatAdj(prefs.getTimeAdj(PrayerCalculator.IDX_FAJR)));
        tvAdjDhuhr.setText(formatAdj(prefs.getTimeAdj(PrayerCalculator.IDX_DHUHR)));
        tvAdjAsr.setText(formatAdj(prefs.getTimeAdj(PrayerCalculator.IDX_ASR)));
        tvAdjMaghrib.setText(formatAdj(prefs.getTimeAdj(PrayerCalculator.IDX_MAGHRIB)));
        tvAdjIsha.setText(formatAdj(prefs.getTimeAdj(PrayerCalculator.IDX_ISHA)));
    }

    private String formatAdj(int mins) {
        return (mins >= 0 ? "+" : "") + mins;
    }

    // ─── Iqama ────────────────────────────────────────────────────────────────

    private void setupIqamaFields() {
        bindIqamaField(R.id.et_iqama_fajr,    PrayerCalculator.IDX_FAJR);
        bindIqamaField(R.id.et_iqama_dhuhr,   PrayerCalculator.IDX_DHUHR);
        bindIqamaField(R.id.et_iqama_asr,     PrayerCalculator.IDX_ASR);
        bindIqamaField(R.id.et_iqama_maghrib, PrayerCalculator.IDX_MAGHRIB);
        bindIqamaField(R.id.et_iqama_isha,    PrayerCalculator.IDX_ISHA);
    }

    private void bindIqamaField(int viewId, int prayerIdx) {
        EditText et = findViewById(viewId);
        et.setText(String.valueOf(prefs.getIqamaOffset(prayerIdx)));
        et.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                try {
                    int mins = Integer.parseInt(et.getText().toString().trim());
                    prefs.setIqamaOffset(prayerIdx, mins);
                } catch (NumberFormatException ignored) {}
            }
        });
    }

    // ─── Jumuah ──────────────────────────────────────────────────────────────

    private void setupJumuahField() {
        EditText et = findViewById(R.id.et_jumuah_time);
        String saved = prefs.getJumuahTime();
        et.setText(saved.isEmpty() ? "" : saved);
        et.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                prefs.setJumuahTime(et.getText().toString().trim());
            }
        });
    }

    // ─── Display ─────────────────────────────────────────────────────────────

    private void setup24hSwitch() {
        SwitchCompat sw = findViewById(R.id.switch_24h);
        sw.setChecked(prefs.isUse24h());
        sw.setOnCheckedChangeListener((b, v) -> prefs.setUse24h(v));
    }

    private void setupAdhanSwitches() {
        SwitchCompat swAdhan = findViewById(R.id.switch_adhan);
        swAdhan.setChecked(prefs.isAdhanEnabled());
        swAdhan.setOnCheckedChangeListener((b, v) -> prefs.setAdhanEnabled(v));

        bindPrayerSwitch(R.id.switch_fajr,    PrayerCalculator.IDX_FAJR);
        bindPrayerSwitch(R.id.switch_dhuhr,   PrayerCalculator.IDX_DHUHR);
        bindPrayerSwitch(R.id.switch_asr,     PrayerCalculator.IDX_ASR);
        bindPrayerSwitch(R.id.switch_maghrib, PrayerCalculator.IDX_MAGHRIB);
        bindPrayerSwitch(R.id.switch_isha,    PrayerCalculator.IDX_ISHA);
    }

    private void bindPrayerSwitch(int viewId, int prayerIdx) {
        SwitchCompat sw = findViewById(viewId);
        sw.setChecked(prefs.isPrayerEnabled(prayerIdx));
        sw.setOnCheckedChangeListener((b, v) -> prefs.setPrayerEnabled(prayerIdx, v));
    }
}

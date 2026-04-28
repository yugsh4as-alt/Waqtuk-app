package com.prayertimes.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.prayertimes.app.calculation.PrayerCalculator;

public class AppPreferences {

    private static final String PREFS_NAME = "prayer_times_prefs";

    private static final String KEY_LATITUDE        = "latitude";
    private static final String KEY_LONGITUDE       = "longitude";
    private static final String KEY_ELEVATION       = "elevation";
    private static final String KEY_LOCATION_NAME   = "location_name";
    private static final String KEY_CALC_METHOD     = "calc_method";
    private static final String KEY_ASR_JURISTIC    = "asr_juristic";
    private static final String KEY_USE_24H         = "use_24h";
    private static final String KEY_ADHAN_ENABLED   = "adhan_enabled";
    private static final String KEY_FAJR_ENABLED    = "fajr_enabled";
    private static final String KEY_DHUHR_ENABLED   = "dhuhr_enabled";
    private static final String KEY_ASR_ENABLED     = "asr_enabled";
    private static final String KEY_MAGHRIB_ENABLED = "maghrib_enabled";
    private static final String KEY_ISHA_ENABLED    = "isha_enabled";
    private static final String KEY_LOCATION_SET    = "location_set";
    private static final String KEY_LANGUAGE        = "language";

    // Iqama offsets in minutes
    private static final String KEY_IQAMA_FAJR    = "iqama_fajr";
    private static final String KEY_IQAMA_DHUHR   = "iqama_dhuhr";
    private static final String KEY_IQAMA_ASR     = "iqama_asr";
    private static final String KEY_IQAMA_MAGHRIB = "iqama_maghrib";
    private static final String KEY_IQAMA_ISHA    = "iqama_isha";
    private static final String KEY_JUMUAH_TIME   = "jumuah_time"; // fractional hours, -1 = use Dhuhr

    private static final double DEFAULT_LAT  = 21.3891;
    private static final double DEFAULT_LON  = 39.8579;
    private static final double DEFAULT_ELEV = 277;

    private final SharedPreferences prefs;

    public AppPreferences(Context ctx) {
        prefs = ctx.getApplicationContext()
                   .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // ─── Location ────────────────────────────────────────────────────────────
    public void saveLocation(double lat, double lon, double elevation, String name) {
        prefs.edit()
             .putFloat(KEY_LATITUDE,  (float) lat)
             .putFloat(KEY_LONGITUDE, (float) lon)
             .putFloat(KEY_ELEVATION, (float) elevation)
             .putString(KEY_LOCATION_NAME, name)
             .putBoolean(KEY_LOCATION_SET, true)
             .apply();
    }

    public double  getLatitude()     { return prefs.getFloat(KEY_LATITUDE,  (float) DEFAULT_LAT); }
    public double  getLongitude()    { return prefs.getFloat(KEY_LONGITUDE, (float) DEFAULT_LON); }
    public double  getElevation()    { return prefs.getFloat(KEY_ELEVATION, (float) DEFAULT_ELEV); }
    public String  getLocationName() { return prefs.getString(KEY_LOCATION_NAME, "Mecca"); }
    public boolean isLocationSet()   { return prefs.getBoolean(KEY_LOCATION_SET, false); }

    // ─── Calculation ─────────────────────────────────────────────────────────
    public void setCalcMethod(int m) { prefs.edit().putInt(KEY_CALC_METHOD, m).apply(); }
    public int  getCalcMethod()      { return prefs.getInt(KEY_CALC_METHOD, PrayerCalculator.METHOD_MWL); }

    public void setAsrJuristic(int j) { prefs.edit().putInt(KEY_ASR_JURISTIC, j).apply(); }
    public int  getAsrJuristic()      { return prefs.getInt(KEY_ASR_JURISTIC, PrayerCalculator.ASR_SHAFI); }

    // ─── Display ─────────────────────────────────────────────────────────────
    public void setUse24h(boolean v) { prefs.edit().putBoolean(KEY_USE_24H, v).apply(); }
    public boolean isUse24h()        { return prefs.getBoolean(KEY_USE_24H, false); }

    public void setLanguage(String lang) { prefs.edit().putString(KEY_LANGUAGE, lang).apply(); }
    public String getLanguage()          { return prefs.getString(KEY_LANGUAGE, "ar"); }

    // ─── Adhan ───────────────────────────────────────────────────────────────
    public void setAdhanEnabled(boolean v) { prefs.edit().putBoolean(KEY_ADHAN_ENABLED, v).apply(); }
    public boolean isAdhanEnabled()        { return prefs.getBoolean(KEY_ADHAN_ENABLED, true); }

    public void setPrayerEnabled(int idx, boolean v) {
        String key = getPrayerKey(idx);
        if (key != null) prefs.edit().putBoolean(key, v).apply();
    }
    public boolean isPrayerEnabled(int idx) {
        String key = getPrayerKey(idx);
        if (key == null) return false;
        return prefs.getBoolean(key, true);
    }
    private String getPrayerKey(int idx) {
        switch (idx) {
            case PrayerCalculator.IDX_FAJR:    return KEY_FAJR_ENABLED;
            case PrayerCalculator.IDX_DHUHR:   return KEY_DHUHR_ENABLED;
            case PrayerCalculator.IDX_ASR:     return KEY_ASR_ENABLED;
            case PrayerCalculator.IDX_MAGHRIB: return KEY_MAGHRIB_ENABLED;
            case PrayerCalculator.IDX_ISHA:    return KEY_ISHA_ENABLED;
            default: return null;
        }
    }

    // ─── Iqama offsets (minutes after Adhan) ─────────────────────────────────
    public void setIqamaOffset(int prayerIdx, int minutes) {
        String key = getIqamaKey(prayerIdx);
        if (key != null) prefs.edit().putInt(key, minutes).apply();
    }
    public int getIqamaOffset(int prayerIdx) {
        String key = getIqamaKey(prayerIdx);
        if (key == null) return 10;
        // defaults: Fajr=17, Maghrib=7, rest=10
        int def = 10;
        if (prayerIdx == PrayerCalculator.IDX_FAJR)    def = 17;
        if (prayerIdx == PrayerCalculator.IDX_MAGHRIB) def = 7;
        return prefs.getInt(key, def);
    }
    private String getIqamaKey(int idx) {
        switch (idx) {
            case PrayerCalculator.IDX_FAJR:    return KEY_IQAMA_FAJR;
            case PrayerCalculator.IDX_DHUHR:   return KEY_IQAMA_DHUHR;
            case PrayerCalculator.IDX_ASR:     return KEY_IQAMA_ASR;
            case PrayerCalculator.IDX_MAGHRIB: return KEY_IQAMA_MAGHRIB;
            case PrayerCalculator.IDX_ISHA:    return KEY_IQAMA_ISHA;
            default: return null;
        }
    }

    // ─── Jumuah ──────────────────────────────────────────────────────────────
    // stored as "HH:MM" string, "" = use Dhuhr time
    public void setJumuahTime(String hhmm) { prefs.edit().putString(KEY_JUMUAH_TIME, hhmm).apply(); }
    public String getJumuahTime()          { return prefs.getString(KEY_JUMUAH_TIME, ""); }

    // ─── Time adjustments (minutes to add/subtract per prayer) ───────────────
    private static final String KEY_ADJ_FAJR    = "adj_fajr";
    private static final String KEY_ADJ_DHUHR   = "adj_dhuhr";
    private static final String KEY_ADJ_ASR     = "adj_asr";
    private static final String KEY_ADJ_MAGHRIB = "adj_maghrib";
    private static final String KEY_ADJ_ISHA    = "adj_isha";
    private static final String KEY_ADJ_SUNRISE = "adj_sunrise";
    private static final String KEY_ADJ_SUNSET  = "adj_sunset";

    public void setTimeAdj(int prayerIdx, int minutes) {
        String key = getAdjKey(prayerIdx);
        if (key != null) prefs.edit().putInt(key, minutes).apply();
    }

    public int getTimeAdj(int prayerIdx) {
        String key = getAdjKey(prayerIdx);
        if (key == null) return 0;
        return prefs.getInt(key, 0);
    }

    private String getAdjKey(int idx) {
        switch (idx) {
            case PrayerCalculator.IDX_FAJR:    return KEY_ADJ_FAJR;
            case PrayerCalculator.IDX_SUNRISE: return KEY_ADJ_SUNRISE;
            case PrayerCalculator.IDX_DHUHR:   return KEY_ADJ_DHUHR;
            case PrayerCalculator.IDX_ASR:     return KEY_ADJ_ASR;
            case PrayerCalculator.IDX_SUNSET:  return KEY_ADJ_SUNSET;
            case PrayerCalculator.IDX_MAGHRIB: return KEY_ADJ_MAGHRIB;
            case PrayerCalculator.IDX_ISHA:    return KEY_ADJ_ISHA;
            default: return null;
        }
    }


    // ─── API Calibration flag ─────────────────────────────────────────────────
    private static final String KEY_API_CALIBRATED  = "api_calibrated";
    private static final String KEY_CALIBRATED_LAT  = "calibrated_lat";
    private static final String KEY_CALIBRATED_LON  = "calibrated_lon";

    public void setApiCalibrated(boolean v, double lat, double lon) {
        prefs.edit()
             .putBoolean(KEY_API_CALIBRATED, v)
             .putFloat(KEY_CALIBRATED_LAT, (float) lat)
             .putFloat(KEY_CALIBRATED_LON, (float) lon)
             .apply();
    }

    public boolean isApiCalibrated() {
        return prefs.getBoolean(KEY_API_CALIBRATED, false);
    }

    /** Returns true if calibration was done at a significantly different location (>20km). */
    public boolean needsRecalibration(double lat, double lon) {
        if (!isApiCalibrated()) return true;
        double calLat = prefs.getFloat(KEY_CALIBRATED_LAT, 0);
        double calLon = prefs.getFloat(KEY_CALIBRATED_LON, 0);
        double distDeg = Math.sqrt(Math.pow(lat - calLat, 2) + Math.pow(lon - calLon, 2));
        return distDeg > 0.2; // ~20 km
    }

}
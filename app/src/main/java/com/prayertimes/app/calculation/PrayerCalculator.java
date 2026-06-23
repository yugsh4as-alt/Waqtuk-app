package com.prayertimes.app.calculation;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Offline prayer time calculation engine.
 *
 * Implements the astronomical algorithms used by:
 *   - Muslim World League (MWL)
 *   - Islamic Society of North America (ISNA)
 *   - Egyptian General Authority of Survey
 *   - Umm al-Qura University, Makkah
 *   - University of Islamic Sciences, Karachi
 *   - Institute of Geophysics, University of Tehran
 *   - Shia Ithna Ashari / Leva Institute, Qum
 *
 * Reference: "Prayer Times Calculation Methods" – praytimes.org
 */
public class PrayerCalculator {

    // ─── Calculation method constants ───────────────────────────────────────
    public static final int METHOD_MWL      = 0;
    public static final int METHOD_ISNA     = 1;
    public static final int METHOD_EGYPT    = 2;
    public static final int METHOD_MAKKAH   = 3;
    public static final int METHOD_KARACHI  = 4;
    public static final int METHOD_TEHRAN   = 5;
    public static final int METHOD_JAFARI   = 6;

    // ─── Asr juristic constants ──────────────────────────────────────────────
    public static final int ASR_SHAFI  = 0; // shadow ratio = 1 (default)
    public static final int ASR_HANAFI = 1; // shadow ratio = 2

    // ─── Prayer index constants ──────────────────────────────────────────────
    public static final int IDX_FAJR    = 0;
    public static final int IDX_SUNRISE = 1;
    public static final int IDX_DHUHR   = 2;
    public static final int IDX_ASR     = 3;
    public static final int IDX_SUNSET  = 4;
    public static final int IDX_MAGHRIB = 5;
    public static final int IDX_ISHA    = 6;

    // ─── Method parameters [fajrAngle, ishaAngle / ishaMinutes, isMagribMinutes] ─
    private static final double[][] METHOD_PARAMS = {
        { 18, 17, 0 },   // MWL
        { 15, 15, 0 },   // ISNA
        { 19.5, 17.5, 0 },// Egypt
        { 18.5, 0, 90 }, // Makkah (isha = 90 min after sunset)
        { 18, 18, 0 },   // Karachi
        { 17.7, 14, 0 }, // Tehran
        { 16, 14, 0 },   // Jafari
    };

    // ─── Inputs ──────────────────────────────────────────────────────────────
    private final double latitude;
    private final double longitude;
    private final double elevation;   // metres above sea level
    private final int    calcMethod;
    private final int    asrJuristic;
    private final double timeZoneOffset; // hours from UTC

    public PrayerCalculator(double latitude, double longitude, double elevation,
                            int calcMethod, int asrJuristic) {
        this.latitude       = latitude;
        this.longitude      = longitude;
        this.elevation      = elevation;
        this.calcMethod     = calcMethod;
        this.asrJuristic    = asrJuristic;
        // Use coordinate-based timezone for accuracy (works on emulators/servers too)
        this.timeZoneOffset = TimezoneDetector.getOffset(latitude, longitude);
    }

    // ─── Public API ─────────────────────────────────────────────────────────

    /**
     * Compute prayer times for the given date.
     * @return double[7] in fractional hours (local time): Fajr, Sunrise, Dhuhr, Asr, Sunset, Maghrib, Isha
     */
    public double[] calculate(int year, int month, int day) {
        double jd = julianDay(year, month, day) - longitude / (15.0 * 24.0);
        double[] times = {5, 6, 12, 13, 18, 18, 18}; // seeds
        // iterate for accuracy
        for (int i = 0; i < 9; i++) {
            times = computePrayerTimes(times, jd);
        }
        return adjustToLocalTime(times);
    }

    /**
     * Helper: get today's prayer times using the device calendar.
     */
    public double[] calculateToday() {
        Calendar cal = Calendar.getInstance();
        return calculate(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH)
        );
    }

    /** Convert fractional hours → "HH:MM" string (24-hour). */
    public static String formatTime24(double time) {
        if (Double.isNaN(time)) return "--:--";
        time = fixHour(time);
        int h = (int) time;
        int m = (int) Math.round((time - h) * 60);
        if (m == 60) { h++; m = 0; }
        if (h == 24) h = 0;
        return String.format("%02d:%02d", h, m);
    }

    /** Convert fractional hours → "hh:MM AM/PM" string. */
    public static String formatTime12(double time) {
        if (Double.isNaN(time)) return "--:--";
        time = fixHour(time);
        int h = (int) time;
        int m = (int) Math.round((time - h) * 60);
        if (m == 60) { h++; m = 0; }
        String ampm = h < 12 ? "AM" : "PM";
        if (h == 0) h = 12;
        else if (h > 12) h -= 12;
        return String.format("%d:%02d %s", h, m, ampm);
    }

    // ─── Core computation ────────────────────────────────────────────────────

    private double[] computePrayerTimes(double[] t, double jd) {
        double[] d = sunPosition(jd + t[IDX_DHUHR] / 24.0);
        double declination = d[0];
        double eqt         = d[1];

        double[] out = new double[7];
        out[IDX_DHUHR]   = computeMidDay(eqt);
        out[IDX_SUNRISE] = computeAstroTime(out[IDX_DHUHR], 0.833 + 0.0347 * Math.sqrt(elevation), 1, declination);
        out[IDX_SUNSET]  = computeAstroTime(out[IDX_DHUHR], 0.833 + 0.0347 * Math.sqrt(elevation), -1, declination);
        out[IDX_FAJR]    = computeAstroTime(out[IDX_DHUHR], METHOD_PARAMS[calcMethod][0], 1, declination);
        out[IDX_ASR]     = computeAsr(out[IDX_DHUHR], declination);
        out[IDX_MAGHRIB] = computeMaghrib(out[IDX_SUNSET]);
        out[IDX_ISHA]    = computeIsha(out[IDX_DHUHR], out[IDX_MAGHRIB], out[IDX_SUNSET], declination);
        return out;
    }

    private double computeMidDay(double eqt) {
        return fixHour(12 - eqt - longitude / 15.0);
    }

    private double computeAstroTime(double midDay, double angle, int direction, double dec) {
        double cosD = Math.cos(deg2Rad(dec)) * Math.cos(deg2Rad(latitude));
        double sinA = -Math.sin(deg2Rad(angle))
                    - Math.sin(deg2Rad(dec)) * Math.sin(deg2Rad(latitude));
        double hr = Math.acos(sinA / cosD) / Math.PI * 12;
        if (Double.isNaN(hr)) return Double.NaN;
        return midDay - direction * hr;
    }

    private double computeAsr(double midDay, double dec) {
        double target = 90 - rad2Deg(Math.atan(asrJuristic + 1.0 / Math.tan(deg2Rad(Math.abs(latitude - dec)))));
        return computeAstroTime(midDay, target, -1, dec);
    }

    private double computeMaghrib(double sunset) {
        double p = METHOD_PARAMS[calcMethod][2];
        if (p > 0) return sunset + p / 60.0; // minutes after sunset
        return sunset; // same as sunset for most methods
    }

    private double computeIsha(double midDay, double maghrib, double sunset, double dec) {
        double p1 = METHOD_PARAMS[calcMethod][1];
        double p2 = METHOD_PARAMS[calcMethod][2];
        if (p2 > 0) {
            // Makkah method: fixed minutes after Maghrib
            return maghrib + p1 / 60.0;
        }
        return computeAstroTime(midDay, p1, -1, dec);
    }

    // ─── Sun position ────────────────────────────────────────────────────────

    /** Returns [declination_deg, equation_of_time_hours]. */
    private double[] sunPosition(double jd) {
        double D  = jd - 2451545.0;
        double g  = fixAngle(357.529 + 0.98560028 * D);
        double q  = fixAngle(280.459 + 0.98564736 * D);
        double L  = fixAngle(q + 1.915 * Math.sin(deg2Rad(g)) + 0.020 * Math.sin(deg2Rad(2 * g)));
        double e  = 23.439 - 0.00000036 * D;
        double RA = rad2Deg(Math.atan2(Math.cos(deg2Rad(e)) * Math.sin(deg2Rad(L)), Math.cos(deg2Rad(L)))) / 15.0;
        double eqt = q / 15.0 - fixHour(RA);
        double dec = rad2Deg(Math.asin(Math.sin(deg2Rad(e)) * Math.sin(deg2Rad(L))));
        return new double[]{dec, eqt};
    }

    // ─── Julian Day ──────────────────────────────────────────────────────────

    private double julianDay(int year, int month, int day) {
        if (month <= 2) { year--; month += 12; }
        int A = year / 100;
        int B = 2 - A + A / 4;
        return (int) (365.25 * (year + 4716)) + (int) (30.6001 * (month + 1)) + day + B - 1524.5;
    }

    // ─── Time zone ───────────────────────────────────────────────────────────

    private double getUtcOffsetHours() {
        TimeZone tz = TimeZone.getDefault();
        Calendar cal = Calendar.getInstance(tz);
        int offsetMs = tz.getOffset(cal.getTimeInMillis());
        return offsetMs / 3_600_000.0;
    }

    private double[] adjustToLocalTime(double[] times) {
        double[] local = new double[7];
        for (int i = 0; i < 7; i++) {
            local[i] = times[i] + timeZoneOffset;
        }
        return local;
    }

    // ─── Math helpers ────────────────────────────────────────────────────────

    private static double deg2Rad(double d) { return d * Math.PI / 180.0; }
    private static double rad2Deg(double r) { return r * 180.0 / Math.PI; }
    private static double fixAngle(double a) { return a - 360.0 * Math.floor(a / 360.0); }
    private static double fixHour(double h)  { return h - 24.0  * Math.floor(h / 24.0);  }
}

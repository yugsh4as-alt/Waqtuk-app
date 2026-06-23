package com.prayertimes.app.utils;

import java.util.Calendar;

public final class TimeUtils {

    private TimeUtils() {}

    /** Current time as fractional hours (local). */
    public static double nowHours() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.HOUR_OF_DAY) + c.get(Calendar.MINUTE) / 60.0
               + c.get(Calendar.SECOND) / 3600.0;
    }

    /** Convert fractional hours → milliseconds from midnight. */
    public static long hoursToMillis(double hours) {
        return (long) (hours * 3_600_000L);
    }

    /** Build a human-readable countdown string: "1h 23m" or "45m". */
    public static String countdown(double fromHours, double toHours) {
        double diff = toHours - fromHours;
        if (diff < 0) diff += 24;
        int totalMinutes = (int) (diff * 60);
        int h = totalMinutes / 60;
        int m = totalMinutes % 60;
        if (h > 0) return h + "h " + m + "m";
        return m + "m";
    }

    /** Returns the index of the next prayer (Fajr/Dhuhr/Asr/Maghrib/Isha only — not Sunrise/Sunset). */
    public static int nextPrayerIndex(double[] times) {
        int[] prayerIndices = {0, 2, 3, 5, 6}; // Fajr, Dhuhr, Asr, Maghrib, Isha
        double now = nowHours();
        for (int idx : prayerIndices) {
            if (times[idx] > now) return idx;
        }
        return 0; // wrap to Fajr tomorrow
    }
}

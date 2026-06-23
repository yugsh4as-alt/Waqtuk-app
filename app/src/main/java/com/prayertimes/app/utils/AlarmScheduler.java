package com.prayertimes.app.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.prayertimes.app.calculation.PrayerCalculator;
import com.prayertimes.app.receiver.AlarmReceiver;

import java.util.Calendar;

public class AlarmScheduler {

    public static final String EXTRA_PRAYER_INDEX  = "prayer_index";
    public static final String EXTRA_PRAYER_NAME   = "prayer_name";
    public static final String EXTRA_IQAMA_OFFSET  = "iqama_offset"; // minutes
    public static final String EXTRA_PRAYER_TIME   = "prayer_time";  // HH:MM string

    private static final int[] ADHAN_PRAYER_INDICES = {
        PrayerCalculator.IDX_FAJR,
        PrayerCalculator.IDX_DHUHR,
        PrayerCalculator.IDX_ASR,
        PrayerCalculator.IDX_MAGHRIB,
        PrayerCalculator.IDX_ISHA
    };

    private static final String[] PRAYER_NAMES_EN = {
        "Fajr", "Sunrise", "Dhuhr", "Asr", "Sunset", "Maghrib", "Isha"
    };

    private AlarmScheduler() {}

    public static void scheduleAll(Context ctx, double[] times, AppPreferences prefs) {
        if (!prefs.isAdhanEnabled()) {
            cancelAll(ctx);
            return;
        }
        for (int idx : ADHAN_PRAYER_INDICES) {
            if (prefs.isPrayerEnabled(idx)) {
                int iqamaOffset = prefs.getIqamaOffset(idx);
                schedule(ctx, idx, PRAYER_NAMES_EN[idx], times[idx], iqamaOffset, prefs);
            } else {
                cancel(ctx, idx);
            }
        }
    }

    public static void schedule(Context ctx, int prayerIndex, String prayerName,
                                double timeHours, int iqamaOffset, AppPreferences prefs) {
        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;

        long triggerAt = buildTriggerMs(timeHours);
        if (triggerAt < System.currentTimeMillis()) return;

        PendingIntent pi = buildPendingIntent(ctx, prayerIndex, prayerName,
                                              iqamaOffset, timeHours, prefs);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (am.canScheduleExactAlarms())
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi);
            else
                am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi);
        } else {
            am.setExact(AlarmManager.RTC_WAKEUP, triggerAt, pi);
        }
    }

    public static void cancel(Context ctx, int prayerIndex) {
        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;
        PendingIntent pi = buildPendingIntent(ctx, prayerIndex, "", 0, 0, null);
        am.cancel(pi);
    }

    public static void cancelAll(Context ctx) {
        for (int idx : ADHAN_PRAYER_INDICES) cancel(ctx, idx);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private static long buildTriggerMs(double timeHours) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, (int) timeHours);
        cal.set(Calendar.MINUTE,      (int) ((timeHours % 1) * 60));
        cal.set(Calendar.SECOND,      0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    /** Format fractional hours → "HH:MM" */
    public static String formatHours(double h, boolean use24h) {
        h = h - 24 * Math.floor(h / 24);
        int hh = (int) h;
        int mm = (int) Math.round((h - hh) * 60);
        if (mm == 60) { hh++; mm = 0; }
        if (hh >= 24)  hh -= 24;
        if (use24h) return String.format("%02d:%02d", hh, mm);
        String ampm = hh < 12 ? "AM" : "PM";
        if (hh == 0) hh = 12; else if (hh > 12) hh -= 12;
        return String.format("%d:%02d %s", hh, mm, ampm);
    }

    private static PendingIntent buildPendingIntent(Context ctx, int prayerIndex,
                                                    String prayerName, int iqamaOffset,
                                                    double timeHours, AppPreferences prefs) {
        Intent intent = new Intent(ctx, AlarmReceiver.class);
        intent.setAction("com.prayertimes.app.PRAYER_ALARM");
        intent.putExtra(EXTRA_PRAYER_INDEX, prayerIndex);
        intent.putExtra(EXTRA_PRAYER_NAME,  prayerName);
        intent.putExtra(EXTRA_IQAMA_OFFSET, iqamaOffset);

        // Build iqama time string for notification
        if (prefs != null && timeHours > 0) {
            boolean use24h = prefs.isUse24h();
            double iqamaHours = timeHours + iqamaOffset / 60.0;
            String iqamaTime = formatHours(iqamaHours, use24h);
            intent.putExtra(EXTRA_PRAYER_TIME, iqamaTime);
        }

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) flags |= PendingIntent.FLAG_IMMUTABLE;
        return PendingIntent.getBroadcast(ctx, prayerIndex, intent, flags);
    }
}

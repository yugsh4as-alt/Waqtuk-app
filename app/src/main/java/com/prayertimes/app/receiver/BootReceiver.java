package com.prayertimes.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.prayertimes.app.calculation.PrayerCalculator;
import com.prayertimes.app.utils.AlarmScheduler;
import com.prayertimes.app.utils.AppPreferences;

/**
 * Re-schedules all prayer alarms after the device reboots.
 * Alarms are lost on reboot — this receiver restores them silently.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent != null ? intent.getAction() : null;
        if (!Intent.ACTION_BOOT_COMPLETED.equals(action)
                && !Intent.ACTION_MY_PACKAGE_REPLACED.equals(action)) return;

        AppPreferences prefs = new AppPreferences(context);
        if (!prefs.isLocationSet()) return;

        PrayerCalculator calc = new PrayerCalculator(
                prefs.getLatitude(),
                prefs.getLongitude(),
                prefs.getElevation(),
                prefs.getCalcMethod(),
                prefs.getAsrJuristic()
        );
        double[] times = calc.calculateToday();
        AlarmScheduler.scheduleAll(context, times, prefs);
    }
}

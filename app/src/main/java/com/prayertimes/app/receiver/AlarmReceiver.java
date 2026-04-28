package com.prayertimes.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.prayertimes.app.audio.AdhanService;
import com.prayertimes.app.utils.AlarmScheduler;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) return;

        String prayerName  = intent.getStringExtra(AlarmScheduler.EXTRA_PRAYER_NAME);
        int    iqamaOffset = intent.getIntExtra(AlarmScheduler.EXTRA_IQAMA_OFFSET, 10);
        String iqamaTime   = intent.getStringExtra(AlarmScheduler.EXTRA_PRAYER_TIME);

        if (prayerName == null) prayerName = "Prayer";
        if (iqamaTime  == null) iqamaTime  = "";

        AdhanService.play(context, prayerName, iqamaOffset, iqamaTime);
    }
}

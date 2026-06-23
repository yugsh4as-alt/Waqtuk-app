package com.prayertimes.app.receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.prayertimes.app.R;
import com.prayertimes.app.ui.MainActivity;
import com.prayertimes.app.utils.IslamicReminderScheduler;

public class IslamicReminderReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "waqtuk_islamic_reminders";

    @Override
    public void onReceive(Context ctx, Intent intent) {
        if (intent == null) return;

        int    id    = intent.getIntExtra(IslamicReminderScheduler.EXTRA_REMINDER_ID, -1);
        String title = intent.getStringExtra(IslamicReminderScheduler.EXTRA_REMINDER_TITLE);
        String body  = intent.getStringExtra(IslamicReminderScheduler.EXTRA_REMINDER_BODY);

        if (title == null || body == null || id == -1) return;

        createChannel(ctx);
        showNotification(ctx, id, title, body);
    }

    private void showNotification(Context ctx, int id, String title, String body) {
        NotificationManager nm =
            (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm == null) return;

        Intent open = new Intent(ctx, MainActivity.class)
            .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) flags |= PendingIntent.FLAG_IMMUTABLE;
        PendingIntent pi = PendingIntent.getActivity(ctx, id, open, flags);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_mosque)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
            .setContentIntent(pi)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setColor(0xC8A84B)
            .setCategory(NotificationCompat.CATEGORY_REMINDER);

        nm.notify(id, builder.build());
    }

    private void createChannel(Context ctx) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;
        NotificationManager nm =
            (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm == null) return;

        NotificationChannel ch = new NotificationChannel(
            CHANNEL_ID,
            "التذكيرات الإسلامية",
            NotificationManager.IMPORTANCE_HIGH
        );
        ch.setDescription("تذكيرات السور والأذكار والأدعية");
        nm.createNotificationChannel(ch);
    }
}

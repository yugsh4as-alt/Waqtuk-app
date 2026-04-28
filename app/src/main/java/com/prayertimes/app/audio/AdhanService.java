package com.prayertimes.app.audio;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.prayertimes.app.R;
import com.prayertimes.app.ui.MainActivity;

/**
 * Foreground service that:
 * 1. Plays the Adhan audio.
 * 2. Shows a rich notification with:
 *    - Prayer name
 *    - Time elapsed since Adhan started (updates every minute)
 *    - Iqama time
 *    - Stop button
 * 3. After Adhan ends: updates notification to show "Iqama in X min".
 */
public class AdhanService extends Service {

    public static final String ACTION_PLAY  = "com.prayertimes.app.PLAY_ADHAN";
    public static final String ACTION_STOP  = "com.prayertimes.app.STOP_ADHAN";
    public static final String EXTRA_PRAYER       = "prayer_name";
    public static final String EXTRA_IQAMA_OFFSET = "iqama_offset";
    public static final String EXTRA_IQAMA_TIME   = "iqama_time";

    private static final String CHANNEL_ID  = "adhan_channel";
    private static final String CHANNEL_ID2 = "iqama_channel";
    private static final int    NOTIF_ID    = 1001;
    private static final int    NOTIF_IQAMA = 1002;

    private MediaPlayer  mediaPlayer;
    private Handler      tickHandler;
    private Runnable     tickRunnable;

    private String prayerName;
    private int    iqamaOffset; // minutes
    private String iqamaTime;  // formatted string e.g. "5:47 AM"
    private long   adhanStartMs;

    // ═════════════════════════════════════════════════════════════════════════
    // Lifecycle
    // ═════════════════════════════════════════════════════════════════════════

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) { stopSelf(); return START_NOT_STICKY; }

        if (ACTION_STOP.equals(intent.getAction())) {
            stopPlayback();
            return START_NOT_STICKY;
        }

        prayerName   = intent.getStringExtra(EXTRA_PRAYER);
        iqamaOffset  = intent.getIntExtra(EXTRA_IQAMA_OFFSET, 10);
        iqamaTime    = intent.getStringExtra(EXTRA_IQAMA_TIME);
        if (prayerName == null) prayerName = "Prayer";
        if (iqamaTime  == null) iqamaTime  = "";
        adhanStartMs = System.currentTimeMillis();

        createChannels();
        startForeground(NOTIF_ID, buildAdhanNotification(0));
        playAdhan();
        startElapsedTicker();
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onDestroy() {
        stopPlayback();
        super.onDestroy();
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Playback
    // ═════════════════════════════════════════════════════════════════════════

    private void playAdhan() {
        if (mediaPlayer != null) { mediaPlayer.release(); mediaPlayer = null; }

        AudioAttributes attrs = new AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build();

        mediaPlayer = MediaPlayer.create(this, R.raw.adhan);
        if (mediaPlayer == null) { stopSelf(); return; }

        mediaPlayer.setAudioAttributes(attrs);
        mediaPlayer.setLooping(false);
        mediaPlayer.setOnCompletionListener(mp -> onAdhanFinished());
        mediaPlayer.setOnErrorListener((mp, w, e) -> { stopSelf(); return true; });
        mediaPlayer.start();
    }

    /** Called when Adhan audio finishes naturally. */
    private void onAdhanFinished() {
        stopElapsedTicker();

        // Release player
        if (mediaPlayer != null) { mediaPlayer.release(); mediaPlayer = null; }

        // Stop foreground (remove Adhan notification)
        stopForeground(true);

        // Show Iqama countdown notification
        if (iqamaOffset > 0) {
            showIqamaNotification();
        }

        stopSelf();
    }

    private void stopPlayback() {
        stopElapsedTicker();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        stopForeground(true);
        stopSelf();
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Elapsed ticker — updates notification every 30 seconds
    // ═════════════════════════════════════════════════════════════════════════

    private void startElapsedTicker() {
        tickHandler  = new Handler(Looper.getMainLooper());
        tickRunnable = new Runnable() {
            @Override public void run() {
                long elapsedSec = (System.currentTimeMillis() - adhanStartMs) / 1000;
                updateAdhanNotification((int) (elapsedSec / 60));
                tickHandler.postDelayed(this, 30_000);
            }
        };
        tickHandler.postDelayed(tickRunnable, 30_000);
    }

    private void stopElapsedTicker() {
        if (tickHandler != null && tickRunnable != null) {
            tickHandler.removeCallbacks(tickRunnable);
        }
    }

    private void updateAdhanNotification(int elapsedMinutes) {
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (nm != null) nm.notify(NOTIF_ID, buildAdhanNotification(elapsedMinutes));
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Notifications
    // ═════════════════════════════════════════════════════════════════════════

    private void createChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (nm == null) return;

            // Adhan channel (ongoing while playing)
            NotificationChannel ch1 = new NotificationChannel(
                CHANNEL_ID, "الأذان", NotificationManager.IMPORTANCE_HIGH);
            ch1.setDescription("يشتغل خلال الأذان");
            ch1.setSound(null, null);
            nm.createNotificationChannel(ch1);

            // Iqama reminder channel
            NotificationChannel ch2 = new NotificationChannel(
                CHANNEL_ID2, "تذكير الإقامة", NotificationManager.IMPORTANCE_HIGH);
            ch2.setDescription("يذكرك بوقت الإقامة بعد الأذان");
            nm.createNotificationChannel(ch2);
        }
    }

    /**
     * Adhan notification shown while audio is playing.
     * Shows: prayer name, elapsed time, iqama time, Stop button.
     */
    private Notification buildAdhanNotification(int elapsedMinutes) {
        PendingIntent openApp = buildOpenAppIntent();
        PendingIntent stopPi  = buildStopIntent();

        // Elapsed line
        String elapsedLine = elapsedMinutes == 0
            ? ""
            : elapsedMinutes == 1
                ? "دقيقة منذ الأذان"
                : elapsedMinutes + " دقيقة منذ الأذان";

        // Iqama line
        String iqamaLine = "";
        if (!iqamaTime.isEmpty()) {
            iqamaLine = "الإقامة الساعة " + iqamaTime; // 🕒
        }

        // Combine into big text
        StringBuilder bigText = new StringBuilder();
        if (!elapsedLine.isEmpty()) bigText.append(elapsedLine);
        if (!iqamaLine.isEmpty()) {
            if (bigText.length() > 0) bigText.append("  •  ");
            bigText.append(iqamaLine);
        }

        return new NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_mosque)
            .setContentTitle(prayerName + " — حان وقت الصلاة") // 🕌
            .setContentText(bigText.length() > 0 ? bigText.toString() : "يعزف الأذان")
            .setStyle(new NotificationCompat.BigTextStyle()
                .bigText(bigText.length() > 0 ? bigText.toString() : "يعزف الأذان"))
            .setContentIntent(openApp)
            .addAction(android.R.drawable.ic_media_pause, "إيقاف الأذان", stopPi)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setOngoing(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setColor(0xC9A84C)
            .build();
    }

    /**
     * Iqama notification shown AFTER Adhan finishes.
     * Shows: "Iqama in X minutes — Prayer name".
     * Auto-dismisses after iqama time passes.
     */
    private void showIqamaNotification() {
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (nm == null) return;

        PendingIntent openApp = buildOpenAppIntent();

        String title = "الإقامة بعد " + iqamaOffset + " دقيقة — " + prayerName;
        String text  = iqamaTime.isEmpty()
            ? "استعد للصلاة"
            : "الإقامة الساعة " + iqamaTime + " • استعد للصلاة";

        Notification notif = new NotificationCompat.Builder(this, CHANNEL_ID2)
            .setSmallIcon(R.drawable.ic_mosque)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
            .setContentIntent(openApp)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setColor(0x1A6B45)
            .setTimeoutAfter(iqamaOffset * 60_000L) // auto-dismiss after iqama time
            .build();

        nm.notify(NOTIF_IQAMA, notif);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // PendingIntent helpers
    // ═════════════════════════════════════════════════════════════════════════

    private PendingIntent buildOpenAppIntent() {
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) flags |= PendingIntent.FLAG_IMMUTABLE;
        return PendingIntent.getActivity(this, 0, i, flags);
    }

    private PendingIntent buildStopIntent() {
        Intent i = new Intent(this, AdhanService.class);
        i.setAction(ACTION_STOP);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) flags |= PendingIntent.FLAG_IMMUTABLE;
        return PendingIntent.getService(this, 1, i, flags);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Static helpers
    // ═════════════════════════════════════════════════════════════════════════

    public static void play(Context ctx, String prayerName, int iqamaOffset, String iqamaTime) {
        Intent intent = new Intent(ctx, AdhanService.class);
        intent.setAction(ACTION_PLAY);
        intent.putExtra(EXTRA_PRAYER,       prayerName);
        intent.putExtra(EXTRA_IQAMA_OFFSET, iqamaOffset);
        intent.putExtra(EXTRA_IQAMA_TIME,   iqamaTime);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            ctx.startForegroundService(intent);
        else
            ctx.startService(intent);
    }

    public static void stop(Context ctx) {
        Intent intent = new Intent(ctx, AdhanService.class);
        intent.setAction(ACTION_STOP);
        ctx.startService(intent);
    }
}

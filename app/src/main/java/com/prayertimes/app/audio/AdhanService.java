package com.prayertimes.app.audio;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.prayertimes.app.R;
import com.prayertimes.app.ui.MainActivity;

public class AdhanService extends Service {

    public static final String ACTION_PLAY         = "com.prayertimes.app.PLAY_ADHAN";
    public static final String ACTION_STOP         = "com.prayertimes.app.STOP_ADHAN";
    public static final String EXTRA_PRAYER        = "prayer_name";
    public static final String EXTRA_IQAMA_OFFSET  = "iqama_offset";
    public static final String EXTRA_IQAMA_TIME    = "iqama_time";

    private static final String CHANNEL_ADHAN  = "waqtuk_adhan";
    private static final String CHANNEL_IQAMA  = "waqtuk_iqama";
    private static final int    NOTIF_ADHAN    = 2001;
    private static final int    NOTIF_IQAMA    = 2002;

    private MediaPlayer       mediaPlayer;
    private AudioManager      audioManager;
    private AudioFocusRequest audioFocusRequest;
    private PowerManager.WakeLock wakeLock;
    private Handler           tickHandler;
    private Runnable          tickRunnable;

    private String  prayerName;
    private int     iqamaOffset;
    private String  iqamaTime;
    private long    adhanStartMs;
    private boolean delivered = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            stopSelf();
            return START_NOT_STICKY;
        }

        if (ACTION_STOP.equals(intent.getAction())) {
            stopAdhan();
            return START_NOT_STICKY;
        }

        if (!ACTION_PLAY.equals(intent.getAction())) {
            stopSelf();
            return START_NOT_STICKY;
        }

        prayerName  = intent.getStringExtra(EXTRA_PRAYER);
        iqamaOffset = intent.getIntExtra(EXTRA_IQAMA_OFFSET, 10);
        iqamaTime   = intent.getStringExtra(EXTRA_IQAMA_TIME);

        if (prayerName == null)  prayerName  = "الصلاة";
        if (iqamaTime  == null)  iqamaTime   = "";

        adhanStartMs = System.currentTimeMillis();
        delivered    = false;

        createChannels();
        acquireWakeLock();
        requestAudioFocus();

        startForeground(NOTIF_ADHAN, buildAdhanNotification(0));
        playAdhan();
        startTicker();

        return START_NOT_STICKY;
    }

    private void playAdhan() {
        releasePlayer();

        try {
            AudioAttributes attrs = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

            mediaPlayer = MediaPlayer.create(this, R.raw.adhan);
            if (mediaPlayer == null) {
                stopSelf();
                return;
            }

            mediaPlayer.setAudioAttributes(attrs);
            mediaPlayer.setLooping(false);
            mediaPlayer.setOnCompletionListener(mp -> onAdhanComplete());
            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                stopAdhan();
                return true;
            });
            mediaPlayer.start();

        } catch (Exception e) {
            stopAdhan();
        }
    }

    private void onAdhanComplete() {
        if (delivered) return;
        delivered = true;

        stopTicker();
        releaseAudioFocus();
        releasePlayer();
        stopForeground(true);

        if (iqamaOffset > 0) {
            showIqamaNotification();
        }

        releaseWakeLock();
        stopSelf();
    }

    private void stopAdhan() {
        if (delivered) return;
        delivered = true;

        stopTicker();
        releaseAudioFocus();
        releasePlayer();
        stopForeground(true);
        releaseWakeLock();
        stopSelf();
    }

    private void startTicker() {
        tickHandler  = new Handler(Looper.getMainLooper());
        tickRunnable = new Runnable() {
            @Override
            public void run() {
                if (delivered) return;
                int elapsedMin = (int) ((System.currentTimeMillis() - adhanStartMs) / 60000);
                NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                if (nm != null) nm.notify(NOTIF_ADHAN, buildAdhanNotification(elapsedMin));
                tickHandler.postDelayed(this, 30_000);
            }
        };
        tickHandler.postDelayed(tickRunnable, 30_000);
    }

    private void stopTicker() {
        if (tickHandler != null && tickRunnable != null) {
            tickHandler.removeCallbacks(tickRunnable);
        }
    }

    private Notification buildAdhanNotification(int elapsedMin) {
        String title = prayerName + " — حان وقت الصلاة";
        StringBuilder body = new StringBuilder();
        if (elapsedMin > 0) body.append(elapsedMin).append(" دقيقة منذ الأذان");
        if (!iqamaTime.isEmpty()) {
            if (body.length() > 0) body.append("  •  ");
            body.append("الإقامة الساعة ").append(iqamaTime);
        }
        if (body.length() == 0) body.append("يعزف الأذان");

        return new NotificationCompat.Builder(this, CHANNEL_ADHAN)
            .setSmallIcon(R.drawable.ic_mosque)
            .setContentTitle(title)
            .setContentText(body.toString())
            .setStyle(new NotificationCompat.BigTextStyle().bigText(body.toString()))
            .setContentIntent(buildOpenAppIntent())
            .addAction(android.R.drawable.ic_delete, "إيقاف الأذان", buildStopIntent())
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setColor(0xC8A84B)
            .build();
    }

    private void showIqamaNotification() {
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (nm == null) return;

        String title = "الإقامة بعد " + iqamaOffset + " دقيقة — " + prayerName;
        String body  = iqamaTime.isEmpty()
            ? "استعد للصلاة"
            : "الإقامة الساعة " + iqamaTime + " • استعد للصلاة";

        Notification n = new NotificationCompat.Builder(this, CHANNEL_IQAMA)
            .setSmallIcon(R.drawable.ic_mosque)
            .setContentTitle(title)
            .setContentText(body)
            .setContentIntent(buildOpenAppIntent())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setColor(0x1B6E48)
            .setTimeoutAfter(iqamaOffset * 60_000L)
            .build();

        nm.notify(NOTIF_IQAMA, n);
    }

    private void createChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (nm == null) return;

        NotificationChannel ch1 = new NotificationChannel(
            CHANNEL_ADHAN, "الأذان", NotificationManager.IMPORTANCE_HIGH);
        ch1.setSound(null, null);
        ch1.enableVibration(false);

        NotificationChannel ch2 = new NotificationChannel(
            CHANNEL_IQAMA, "تذكير الإقامة", NotificationManager.IMPORTANCE_HIGH);

        nm.createNotificationChannel(ch1);
        nm.createNotificationChannel(ch2);
    }

    private void requestAudioFocus() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioAttributes attrs = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
            audioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                .setAudioAttributes(attrs)
                .setAcceptsDelayedFocusGain(false)
                .setOnAudioFocusChangeListener(focusChange -> {})
                .build();
            audioManager.requestAudioFocus(audioFocusRequest);
        } else {
            audioManager.requestAudioFocus(
                focusChange -> {},
                AudioManager.STREAM_ALARM,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
            );
        }
    }

    private void releaseAudioFocus() {
        if (audioManager == null) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && audioFocusRequest != null) {
            audioManager.abandonAudioFocusRequest(audioFocusRequest);
        } else {
            audioManager.abandonAudioFocus(focusChange -> {});
        }
    }

    private void acquireWakeLock() {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (pm == null) return;
        wakeLock = pm.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK, "Waqtuk:AdhanWakeLock");
        wakeLock.acquire(10 * 60 * 1000L);
    }

    private void releaseWakeLock() {
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
        wakeLock = null;
    }

    private void releasePlayer() {
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) mediaPlayer.stop();
                mediaPlayer.release();
            } catch (Exception ignored) {}
            mediaPlayer = null;
        }
    }

    private PendingIntent buildOpenAppIntent() {
        Intent i = new Intent(this, MainActivity.class)
            .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT
            | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0);
        return PendingIntent.getActivity(this, 0, i, flags);
    }

    private PendingIntent buildStopIntent() {
        Intent i = new Intent(this, AdhanService.class).setAction(ACTION_STOP);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT
            | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0);
        return PendingIntent.getService(this, 1, i, flags);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onDestroy() {
        stopAdhan();
        super.onDestroy();
    }

    public static void play(Context ctx, String prayerName, int iqamaOffset, String iqamaTime) {
        Intent i = new Intent(ctx, AdhanService.class)
            .setAction(ACTION_PLAY)
            .putExtra(EXTRA_PRAYER, prayerName)
            .putExtra(EXTRA_IQAMA_OFFSET, iqamaOffset)
            .putExtra(EXTRA_IQAMA_TIME, iqamaTime);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ctx.startForegroundService(i);
        } else {
            ctx.startService(i);
        }
    }

    public static void stop(Context ctx) {
        ctx.startService(new Intent(ctx, AdhanService.class).setAction(ACTION_STOP));
    }
}

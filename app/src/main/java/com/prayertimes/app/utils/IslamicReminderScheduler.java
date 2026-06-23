package com.prayertimes.app.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.prayertimes.app.calculation.PrayerCalculator;
import com.prayertimes.app.receiver.IslamicReminderReceiver;

import java.util.Calendar;

public class IslamicReminderScheduler {

    public static final String EXTRA_REMINDER_ID    = "reminder_id";
    public static final String EXTRA_REMINDER_TITLE = "reminder_title";
    public static final String EXTRA_REMINDER_BODY  = "reminder_body";
    public static final String EXTRA_REMINDER_TYPE  = "reminder_type";

    public static final int ID_FRIDAY_KAHF          = 100;
    public static final int ID_FAJR_MORNING_DHIKR   = 101;
    public static final int ID_AFTER_FAJR_AYAT      = 102;
    public static final int ID_FRIDAY_DHUHA         = 103;
    public static final int ID_BEFORE_MAGHRIB_DUA   = 104;
    public static final int ID_AFTER_ISHA_MULK      = 105;
    public static final int ID_MONDAY_THURSDAY_FAST = 106;
    public static final int ID_LAST_HOUR_FRIDAY     = 107;

    private static final String ACTION = "com.prayertimes.app.ISLAMIC_REMINDER";

    private IslamicReminderScheduler() {}

    public static void scheduleAll(Context ctx, double[] prayerTimes) {
        Calendar now = Calendar.getInstance();
        int dayOfWeek = now.get(Calendar.DAY_OF_WEEK);

        scheduleAfterFajrMorningAdhkar(ctx, prayerTimes);
        scheduleAfterFajrAyatAlKursi(ctx, prayerTimes);
        scheduleBeforeMaghribDua(ctx, prayerTimes);
        scheduleAfterIshaSurahMulk(ctx, prayerTimes);

        if (dayOfWeek == Calendar.FRIDAY) {
            scheduleFridayKahf(ctx, prayerTimes);
            scheduleFridayDhuha(ctx, prayerTimes);
            scheduleLastHourFriday(ctx, prayerTimes);
        }

        if (dayOfWeek == Calendar.MONDAY || dayOfWeek == Calendar.THURSDAY) {
            scheduleMondayThursdayFast(ctx);
        }
    }

    public static void cancelAll(Context ctx) {
        int[] ids = {
            ID_FRIDAY_KAHF, ID_FAJR_MORNING_DHIKR, ID_AFTER_FAJR_AYAT,
            ID_FRIDAY_DHUHA, ID_BEFORE_MAGHRIB_DUA, ID_AFTER_ISHA_MULK,
            ID_MONDAY_THURSDAY_FAST, ID_LAST_HOUR_FRIDAY
        };
        for (int id : ids) {
            cancelReminder(ctx, id);
        }
    }

    private static void scheduleAfterFajrMorningAdhkar(Context ctx, double[] times) {
        double fajr       = times[PrayerCalculator.IDX_FAJR];
        double triggerTime = fajr + 10.0 / 60.0;

        schedule(ctx,
            ID_FAJR_MORNING_DHIKR,
            "أذكار الصباح",
            "حان وقت أذكار الصباح — ابدأ يومك بذكر الله",
            "سَيِّدُ الِاسْتِغْفَارِ: اللَّهُمَّ أَنْتَ رَبِّي لا إِلَهَ إِلا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ",
            0,
            triggerTime
        );
    }

    private static void scheduleAfterFajrAyatAlKursi(Context ctx, double[] times) {
        double fajr       = times[PrayerCalculator.IDX_FAJR];
        double sunrise    = times[PrayerCalculator.IDX_SUNRISE];
        double midpoint   = fajr + (sunrise - fajr) / 2.0;

        schedule(ctx,
            ID_AFTER_FAJR_AYAT,
            "آية الكرسي",
            "مَن قرأ آية الكرسي دبر كل صلاة مكتوبة لم يمنعه من دخول الجنة إلا أن يموت",
            "اللَّهُ لَا إِلَهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ",
            0,
            midpoint
        );
    }

    private static void scheduleFridayKahf(Context ctx, double[] times) {
        double fajr = times[PrayerCalculator.IDX_FAJR];
        double triggerTime = fajr + 30.0 / 60.0;

        schedule(ctx,
            ID_FRIDAY_KAHF,
            "سورة الكهف — يوم الجمعة",
            "مَن قرأ سورة الكهف يوم الجمعة أضاء له من النور ما بين الجمعتين",
            "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيمِ — الْحَمْدُ لِلَّهِ الَّذِي أَنزَلَ عَلَى عَبْدِهِ الْكِتَابَ",
            0,
            triggerTime
        );
    }

    private static void scheduleFridayDhuha(Context ctx, double[] times) {
        double fajr    = times[PrayerCalculator.IDX_FAJR];
        double sunrise = times[PrayerCalculator.IDX_SUNRISE];
        double dhuha   = sunrise + (times[PrayerCalculator.IDX_DHUHR] - sunrise) * 0.4;

        schedule(ctx,
            ID_FRIDAY_DHUHA,
            "صلاة الضحى — يوم الجمعة",
            "صلِّ ركعتين أو أربعاً لصلاة الضحى، فهي صدقة عن كل مفصل في جسدك",
            "وَالضُّحَى — وَاللَّيْلِ إِذَا سَجَى — مَا وَدَّعَكَ رَبُّكَ وَمَا قَلَى",
            0,
            dhuha
        );
    }

    private static void scheduleLastHourFriday(Context ctx, double[] times) {
        double maghrib = times[PrayerCalculator.IDX_MAGHRIB];
        double triggerTime = maghrib - 1.0;

        if (triggerTime > 0) {
            schedule(ctx,
                ID_LAST_HOUR_FRIDAY,
                "آخر ساعة من يوم الجمعة",
                "اغتنم الساعة الأخيرة من يوم الجمعة — فيها ساعة لا يوافقها عبد مسلم يسأل الله إلا أعطاه",
                "اللَّهُمَّ إِنِّي أَسْأَلُكَ مِنْ فَضْلِكَ الْعَظِيمِ، فَإِنَّهُ لَا يَمْلِكُهُ إِلَّا أَنْتَ",
                0,
                triggerTime
            );
        }
    }

    private static void scheduleBeforeMaghribDua(Context ctx, double[] times) {
        double maghrib     = times[PrayerCalculator.IDX_MAGHRIB];
        double triggerTime = maghrib - 15.0 / 60.0;

        if (triggerTime > 0) {
            schedule(ctx,
                ID_BEFORE_MAGHRIB_DUA,
                "دعاء قبل المغرب",
                "لا ترد الدعوة بين الأذان والإقامة — ادع الله الآن",
                "اللَّهُمَّ إِنَّكَ عَفُوٌّ تُحِبُّ الْعَفْوَ فَاعْفُ عَنِّي — رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً",
                1,
                triggerTime
            );
        }
    }

    private static void scheduleAfterIshaSurahMulk(Context ctx, double[] times) {
        double isha        = times[PrayerCalculator.IDX_ISHA];
        double triggerTime = isha + 20.0 / 60.0;

        schedule(ctx,
            ID_AFTER_ISHA_MULK,
            "سورة الملك — قبل النوم",
            "مَن قرأ سورة الملك كل ليلة منعه الله من عذاب القبر",
            "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيمِ — تَبَارَكَ الَّذِي بِيَدِهِ الْمُلْكُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ",
            2,
            triggerTime
        );
    }

    private static void scheduleMondayThursdayFast(Context ctx) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 5);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        if (cal.getTimeInMillis() < System.currentTimeMillis()) return;

        String day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY
            ? "الاثنين" : "الخميس";

        Intent intent = new Intent(ctx, IslamicReminderReceiver.class)
            .setAction(ACTION)
            .putExtra(EXTRA_REMINDER_ID,    ID_MONDAY_THURSDAY_FAST)
            .putExtra(EXTRA_REMINDER_TITLE, "صيام يوم " + day)
            .putExtra(EXTRA_REMINDER_BODY,  "كان النبي ﷺ يصوم الاثنين والخميس — النية الآن")
            .putExtra(EXTRA_REMINDER_TYPE,  1);

        setAlarm(ctx, ID_MONDAY_THURSDAY_FAST, cal.getTimeInMillis(), intent);
    }

    private static void schedule(Context ctx, int id, String title,
                                  String body, String arabic, int type, double timeHours) {
        long triggerMs = hoursToMs(timeHours);
        if (triggerMs < System.currentTimeMillis()) return;

        Intent intent = new Intent(ctx, IslamicReminderReceiver.class)
            .setAction(ACTION)
            .putExtra(EXTRA_REMINDER_ID,    id)
            .putExtra(EXTRA_REMINDER_TITLE, title)
            .putExtra(EXTRA_REMINDER_BODY,  body)
            .putExtra(EXTRA_REMINDER_TYPE,  type);

        setAlarm(ctx, id, triggerMs, intent);
    }

    private static void setAlarm(Context ctx, int id, long triggerMs, Intent intent) {
        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) flags |= PendingIntent.FLAG_IMMUTABLE;

        PendingIntent pi = PendingIntent.getBroadcast(ctx, id, intent, flags);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (am.canScheduleExactAlarms())
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMs, pi);
            else
                am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMs, pi);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMs, pi);
        } else {
            am.setExact(AlarmManager.RTC_WAKEUP, triggerMs, pi);
        }
    }

    private static void cancelReminder(Context ctx, int id) {
        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) flags |= PendingIntent.FLAG_IMMUTABLE;
        Intent intent = new Intent(ctx, IslamicReminderReceiver.class).setAction(ACTION);
        PendingIntent pi = PendingIntent.getBroadcast(ctx, id, intent, flags);
        am.cancel(pi);
    }

    private static long hoursToMs(double hours) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, (int) hours);
        cal.set(Calendar.MINUTE, (int) ((hours % 1) * 60));
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }
}

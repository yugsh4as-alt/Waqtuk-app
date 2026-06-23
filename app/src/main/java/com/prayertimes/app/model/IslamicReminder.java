package com.prayertimes.app.model;

public class IslamicReminder {

    public static final int TYPE_SURAH     = 0;
    public static final int TYPE_DHIKR     = 1;
    public static final int TYPE_HADITH    = 2;

    public final String title;
    public final String body;
    public final String arabic;
    public final int    type;
    public final int    notifId;

    public IslamicReminder(String title, String body, String arabic, int type, int notifId) {
        this.title   = title;
        this.body    = body;
        this.arabic  = arabic;
        this.type    = type;
        this.notifId = notifId;
    }
}

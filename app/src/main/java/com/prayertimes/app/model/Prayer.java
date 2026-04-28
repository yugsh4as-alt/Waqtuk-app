package com.prayertimes.app.model;

public class Prayer {
    public final String  name;
    public final double  timeHours;
    public final String  timeFormatted;
    public final String  iqamaFormatted; // null for Sunrise/Sunset
    public final boolean isAdhan;
    public final int     iconRes;

    public Prayer(String name, double timeHours, String timeFormatted,
                  String iqamaFormatted, boolean isAdhan, int iconRes) {
        this.name           = name;
        this.timeHours      = timeHours;
        this.timeFormatted  = timeFormatted;
        this.iqamaFormatted = iqamaFormatted;
        this.isAdhan        = isAdhan;
        this.iconRes        = iconRes;
    }
}

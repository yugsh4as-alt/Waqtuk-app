package com.prayertimes.app.calculation;

/**
 * Returns the correct UTC offset (in hours) for a given coordinate.
 * This ensures correct prayer times regardless of device timezone setting
 * (e.g. when running on emulators/servers set to UTC).
 *
 * Uses geographic bounding boxes for all Muslim-majority regions + major world zones.
 */
public class TimezoneDetector {

    private TimezoneDetector() {}

    /**
     * Returns UTC offset in hours for the given coordinates.
     * Falls back to longitude-based estimate if no region matches.
     */
    public static double getOffset(double lat, double lon) {

        // ── North Africa ──────────────────────────────────────────────────
        // Algeria, Morocco, Tunisia, Mauritania = UTC+1
        if (inBox(lat, lon, 18, 38, -18, 14))  return 1;
        // Libya = UTC+2
        if (inBox(lat, lon, 19, 34, 9, 26))    return 2;
        // Egypt = UTC+2
        if (inBox(lat, lon, 22, 32, 24, 38))   return 2;
        // Sudan = UTC+3
        if (inBox(lat, lon, 8, 24, 24, 40))    return 3;
        // Somalia, Djibouti, Eritrea, Ethiopia = UTC+3
        if (inBox(lat, lon, 5, 18, 38, 52))    return 3;

        // ── Middle East ───────────────────────────────────────────────────
        // Morocco (Canary Islands area) = UTC+1
        if (inBox(lat, lon, 27, 36, -18, -13)) return 1;
        // Jordan, Lebanon, Syria, Palestine, Israel = UTC+2 (UTC+3 DST)
        if (inBox(lat, lon, 29, 38, 34, 43))   return 2;
        // Iraq, Saudi Arabia, Kuwait, Qatar, Bahrain, Yemen = UTC+3
        if (inBox(lat, lon, 12, 38, 36, 56))   return 3;
        // UAE, Oman = UTC+4
        if (inBox(lat, lon, 16, 27, 51, 60))   return 4;
        // Oman east = UTC+4
        if (inBox(lat, lon, 17, 26, 56, 60))   return 4;

        // ── Turkey = UTC+3 ────────────────────────────────────────────────
        if (inBox(lat, lon, 35, 43, 25, 45))   return 3;

        // ── Iran = UTC+3.5 ────────────────────────────────────────────────
        if (inBox(lat, lon, 25, 40, 44, 64))   return 3.5;

        // ── Central Asia ──────────────────────────────────────────────────
        // Turkmenistan, Tajikistan = UTC+5
        if (inBox(lat, lon, 36, 43, 52, 68))   return 5;
        // Uzbekistan, Kyrgyzstan = UTC+5
        if (inBox(lat, lon, 37, 46, 55, 80))   return 5;
        // Kazakhstan west = UTC+5
        if (inBox(lat, lon, 40, 56, 50, 70))   return 5;
        // Kazakhstan east = UTC+6
        if (inBox(lat, lon, 40, 56, 70, 90))   return 6;

        // ── South Asia ────────────────────────────────────────────────────
        // Pakistan = UTC+5
        if (inBox(lat, lon, 23, 38, 60, 78))   return 5;
        // Afghanistan = UTC+4.5
        if (inBox(lat, lon, 29, 39, 60, 75))   return 4.5;
        // India = UTC+5.5
        if (inBox(lat, lon, 8, 37, 68, 98))    return 5.5;
        // Bangladesh = UTC+6
        if (inBox(lat, lon, 20, 27, 88, 93))   return 6;
        // Maldives = UTC+5
        if (inBox(lat, lon, -1, 8, 72, 74))    return 5;

        // ── Southeast Asia ────────────────────────────────────────────────
        // Indonesia west (Sumatra, Java) = UTC+7
        if (inBox(lat, lon, -11, 6, 95, 115))  return 7;
        // Indonesia central = UTC+8
        if (inBox(lat, lon, -11, 3, 115, 125)) return 8;
        // Indonesia east = UTC+9
        if (inBox(lat, lon, -11, 1, 125, 142)) return 9;
        // Malaysia, Brunei, Singapore = UTC+8
        if (inBox(lat, lon, 0, 8, 99, 120))    return 8;
        // Philippines = UTC+8
        if (inBox(lat, lon, 4, 22, 116, 128))  return 8;
        // Thailand, Cambodia, Vietnam = UTC+7
        if (inBox(lat, lon, 0, 25, 98, 110))   return 7;
        // Myanmar = UTC+6.5
        if (inBox(lat, lon, 9, 29, 92, 102))   return 6.5;

        // ── East Asia ─────────────────────────────────────────────────────
        // China, Taiwan = UTC+8
        if (inBox(lat, lon, 18, 55, 73, 135))  return 8;
        // Japan, South Korea = UTC+9
        if (inBox(lat, lon, 24, 46, 129, 148)) return 9;

        // ── Caucasus ──────────────────────────────────────────────────────
        // Azerbaijan = UTC+4
        if (inBox(lat, lon, 38, 42, 44, 51))   return 4;
        // Georgia, Armenia = UTC+4
        if (inBox(lat, lon, 38, 44, 40, 47))   return 4;

        // ── Russia ────────────────────────────────────────────────────────
        if (inBox(lat, lon, 42, 58, 36, 45))   return 3;  // Moscow time
        if (inBox(lat, lon, 42, 58, 45, 60))   return 5;  // Ekaterinburg
        if (inBox(lat, lon, 50, 75, 60, 90))   return 6;
        if (inBox(lat, lon, 50, 75, 90, 120))  return 8;

        // ── Europe ────────────────────────────────────────────────────────
        // UK, Portugal = UTC+0 (UTC+1 DST, but we use standard)
        if (inBox(lat, lon, 36, 62, -12, 3))   return 1;  // CET countries (France, Spain, Germany etc)
        if (inBox(lat, lon, 50, 62, -12, 3))   return 0;  // UK/Ireland
        // Eastern Europe = UTC+2
        if (inBox(lat, lon, 36, 60, 14, 30))   return 2;
        // Finland, Baltics = UTC+2
        if (inBox(lat, lon, 53, 72, 20, 32))   return 2;

        // ── Americas ──────────────────────────────────────────────────────
        if (inBox(lat, lon, 24, 50, -130, -115)) return -8; // PST
        if (inBox(lat, lon, 24, 50, -115, -100)) return -7; // MST
        if (inBox(lat, lon, 24, 50, -100, -85))  return -6; // CST
        if (inBox(lat, lon, 24, 50, -85, -65))   return -5; // EST
        if (inBox(lat, lon, 42, 60, -80, -52))   return -5; // Canada EST
        if (inBox(lat, lon, 49, 60, -120, -80))  return -7; // Canada MST/PST
        if (inBox(lat, lon, -5, 24, -90, -60))   return -5; // Caribbean/C. America
        if (inBox(lat, lon, -35, 6, -82, -34))   return -5; // South America west
        if (inBox(lat, lon, -35, 6, -50, -34))   return -3; // Brazil east

        // ── Australia ────────────────────────────────────────────────────
        if (inBox(lat, lon, -44, -10, 113, 132)) return 8;  // Perth
        if (inBox(lat, lon, -44, -10, 132, 142)) return 9.5;// Adelaide/Darwin
        if (inBox(lat, lon, -44, -10, 142, 155)) return 10; // Sydney/Melbourne
        // New Zealand = UTC+12
        if (inBox(lat, lon, -48, -33, 166, 180)) return 12;

        // ── West Africa ───────────────────────────────────────────────────
        if (inBox(lat, lon, 0, 20, -18, 5))    return 0;   // Senegal, Mali, Guinea
        if (inBox(lat, lon, 0, 20, 5, 20))     return 1;   // Nigeria, Niger, Cameroon
        if (inBox(lat, lon, 0, 15, 20, 40))    return 2;   // Chad, CAR
        if (inBox(lat, lon, -35, 5, -50, -30)) return -3;  // Brazil
        if (inBox(lat, lon, -35, 5, -75, -50)) return -4;  // Bolivia, Paraguay

        // ── Fallback: longitude-based estimate ────────────────────────────
        return Math.round(lon / 15.0);
    }

    private static boolean inBox(double lat, double lon,
                                  double minLat, double maxLat,
                                  double minLon, double maxLon) {
        return lat >= minLat && lat <= maxLat
            && lon >= minLon && lon <= maxLon;
    }
}

package com.prayertimes.app.calculation;

/**
 * Automatically selects the best prayer calculation method
 * based on geographic coordinates (latitude/longitude).
 *
 * Coverage:
 *  - Saudi Arabia, Gulf           → METHOD_MAKKAH
 *  - North Africa (Algeria, Morocco, Tunisia, Libya, Egypt, Sudan) → METHOD_EGYPT
 *  - Iran                         → METHOD_TEHRAN
 *  - Pakistan, Afghanistan, India → METHOD_KARACHI
 *  - North America, Europe        → METHOD_MWL
 *  - Rest of world                → METHOD_MWL (safe default)
 */
public class MethodDetector {

    private MethodDetector() {}

    /**
     * Returns the recommended PrayerCalculator method constant
     * for the given coordinates.
     */
    public static int detect(double lat, double lon) {

        // ── Saudi Arabia & Yemen ──────────────────────────────────────────
        if (inBox(lat, lon, 16, 32, 36, 56)) return PrayerCalculator.METHOD_MAKKAH;

        // ── UAE, Kuwait, Qatar, Bahrain, Oman ────────────────────────────
        if (inBox(lat, lon, 22, 26, 51, 60)) return PrayerCalculator.METHOD_MAKKAH;
        if (inBox(lat, lon, 22, 32, 44, 60)) return PrayerCalculator.METHOD_MAKKAH;

        // ── Iraq ──────────────────────────────────────────────────────────
        if (inBox(lat, lon, 29, 38, 38, 49)) return PrayerCalculator.METHOD_MWL;

        // ── Iran ──────────────────────────────────────────────────────────
        if (inBox(lat, lon, 25, 40, 44, 64)) return PrayerCalculator.METHOD_TEHRAN;

        // ── Egypt ─────────────────────────────────────────────────────────
        if (inBox(lat, lon, 22, 32, 24, 38)) return PrayerCalculator.METHOD_EGYPT;

        // ── Algeria, Morocco, Tunisia, Libya ─────────────────────────────
        if (inBox(lat, lon, 18, 38, -18, 14)) return PrayerCalculator.METHOD_EGYPT;

        // ── Sudan, Mauritania, Mali, Niger, Chad ─────────────────────────
        if (inBox(lat, lon, 10, 24, -17, 40)) return PrayerCalculator.METHOD_EGYPT;

        // ── Somalia, Djibouti, Ethiopia, Eritrea ─────────────────────────
        if (inBox(lat, lon, 5, 18, 38, 52)) return PrayerCalculator.METHOD_MAKKAH;

        // ── Pakistan, Afghanistan ─────────────────────────────────────────
        if (inBox(lat, lon, 23, 38, 60, 75)) return PrayerCalculator.METHOD_KARACHI;

        // ── India (Muslim-majority regions) ──────────────────────────────
        if (inBox(lat, lon, 8, 37, 68, 98)) return PrayerCalculator.METHOD_KARACHI;

        // ── Bangladesh ────────────────────────────────────────────────────
        if (inBox(lat, lon, 20, 27, 88, 93)) return PrayerCalculator.METHOD_KARACHI;

        // ── Indonesia, Malaysia, Brunei ───────────────────────────────────
        if (inBox(lat, lon, -11, 8, 95, 142)) return PrayerCalculator.METHOD_MWL;

        // ── Turkey ────────────────────────────────────────────────────────
        if (inBox(lat, lon, 35, 43, 25, 45)) return PrayerCalculator.METHOD_MWL;

        // ── Central Asia ──────────────────────────────────────────────────
        if (inBox(lat, lon, 37, 56, 45, 90)) return PrayerCalculator.METHOD_MWL;

        // ── Russia (Muslim regions) ───────────────────────────────────────
        if (inBox(lat, lon, 42, 58, 38, 65)) return PrayerCalculator.METHOD_MWL;

        // ── North America ─────────────────────────────────────────────────
        if (lon < -60 && lat > 15) return PrayerCalculator.METHOD_ISNA;

        // ── Europe ────────────────────────────────────────────────────────
        if (lat > 36 && lat < 72 && lon > -12 && lon < 45) return PrayerCalculator.METHOD_MWL;

        // ── Default ───────────────────────────────────────────────────────
        return PrayerCalculator.METHOD_MWL;
    }

    /**
     * Returns the recommended Asr juristic method for the given coordinates.
     * Maliki, Shafi, Hanbali → ASR_SHAFI (shadow factor 1)
     * Hanafi                 → ASR_HANAFI (shadow factor 2)
     * Hanafi dominant regions: Turkey, Central Asia, South Asia, Balkans
     */
    public static int detectAsr(double lat, double lon) {
        // Turkey
        if (inBox(lat, lon, 35, 43, 25, 45)) return PrayerCalculator.ASR_HANAFI;
        // Central Asia (Kazakhstan, Uzbekistan, Kyrgyzstan, Tajikistan, Turkmenistan)
        if (inBox(lat, lon, 37, 56, 45, 90)) return PrayerCalculator.ASR_HANAFI;
        // Pakistan, Bangladesh, Afghanistan (Hanafi)
        if (inBox(lat, lon, 20, 38, 60, 93)) return PrayerCalculator.ASR_HANAFI;
        // Balkans (Bosnia, Albania, Kosovo)
        if (inBox(lat, lon, 38, 47, 13, 25)) return PrayerCalculator.ASR_HANAFI;
        // Russia Muslim regions
        if (inBox(lat, lon, 42, 58, 38, 65)) return PrayerCalculator.ASR_HANAFI;
        // Default: Shafi/Maliki/Hanbali
        return PrayerCalculator.ASR_SHAFI;
    }

    /**
     * Check if (lat, lon) is inside the bounding box
     * [minLat, maxLat, minLon, maxLon].
     */
    private static boolean inBox(double lat, double lon,
                                  double minLat, double maxLat,
                                  double minLon, double maxLon) {
        return lat >= minLat && lat <= maxLat
            && lon >= minLon && lon <= maxLon;
    }
}

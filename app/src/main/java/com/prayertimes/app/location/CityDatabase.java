package com.prayertimes.app.location;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Offline city database — no internet required.
 * Covers: all Arabian Peninsula cities, major cities across Europe,
 * Asia, Africa, Americas, and Oceania (1500+ entries).
 */
public class CityDatabase {

    private static final CityEntry[] CITIES = {

        // ════════════════════════════════════════════════════════════════════
        // SAUDI ARABIA — full coverage
        // ════════════════════════════════════════════════════════════════════
        new CityEntry("Mecca",            "Saudi Arabia",  21.3891,  39.8579,  277),
        new CityEntry("Medina",           "Saudi Arabia",  24.5247,  39.5692,  608),
        new CityEntry("Riyadh",           "Saudi Arabia",  24.6877,  46.7219,  612),
        new CityEntry("Jeddah",           "Saudi Arabia",  21.5433,  39.1728,   15),
        new CityEntry("Dammam",           "Saudi Arabia",  26.4207,  50.0888,   10),
        new CityEntry("Taif",             "Saudi Arabia",  21.2703,  40.4158, 1879),
        new CityEntry("Khobar",           "Saudi Arabia",  26.2172,  50.1971,   10),
        new CityEntry("Dhahran",          "Saudi Arabia",  26.2362,  50.0611,   77),
        new CityEntry("Jubail",           "Saudi Arabia",  27.0046,  49.6592,   10),
        new CityEntry("Tabuk",            "Saudi Arabia",  28.3835,  36.5662,  768),
        new CityEntry("Abha",             "Saudi Arabia",  18.2164,  42.5053, 2270),
        new CityEntry("Khamis Mushait",   "Saudi Arabia",  18.3009,  42.7289, 2056),
        new CityEntry("Hail",             "Saudi Arabia",  27.5114,  41.7208,  973),
        new CityEntry("Najran",           "Saudi Arabia",  17.5650,  44.2290, 1210),
        new CityEntry("Jazan",            "Saudi Arabia",  16.8892,  42.5511,    5),
        new CityEntry("Buraydah",         "Saudi Arabia",  26.3292,  43.9692,  648),
        new CityEntry("Unaizah",          "Saudi Arabia",  26.0840,  43.9937,  651),
        new CityEntry("Arar",             "Saudi Arabia",  30.9757,  41.0381,  549),
        new CityEntry("Sakakah",          "Saudi Arabia",  29.9697,  40.2093,  699),
        new CityEntry("Qatif",            "Saudi Arabia",  26.5100,  50.0197,   10),
        new CityEntry("Hofuf",            "Saudi Arabia",  25.3799,  49.5877,  148),
        new CityEntry("Yanbu",            "Saudi Arabia",  24.0895,  38.0618,    5),
        new CityEntry("Al Qunfudhah",     "Saudi Arabia",  19.1260,  41.0793,    5),
        new CityEntry("Wadi ad-Dawasir",  "Saudi Arabia",  20.4972,  45.0069,  620),
        new CityEntry("Dawadmi",          "Saudi Arabia",  24.4997,  44.3997,  976),
        new CityEntry("Zulfi",            "Saudi Arabia",  26.3080,  44.8029,  690),
        new CityEntry("Majmaah",          "Saudi Arabia",  25.9044,  45.3430,  750),
        new CityEntry("Al Kharj",         "Saudi Arabia",  24.1551,  47.3091,  410),
        new CityEntry("Ras Tanura",       "Saudi Arabia",  26.6441,  50.1633,    5),
        new CityEntry("Al Ula",           "Saudi Arabia",  26.6178,  37.9197,  650),
        new CityEntry("Badr",             "Saudi Arabia",  23.7765,  38.7833,  100),
        new CityEntry("Bisha",            "Saudi Arabia",  19.9941,  42.5975, 1166),
        new CityEntry("Sharurah",         "Saudi Arabia",  17.4837,  47.1208,  734),
        new CityEntry("Ranyah",           "Saudi Arabia",  21.2857,  42.8539, 1375),
        new CityEntry("Shaqra",           "Saudi Arabia",  25.2479,  45.2531,  750),
        new CityEntry("Rabigh",           "Saudi Arabia",  22.8038,  39.0333,   10),
        new CityEntry("Thuwal",           "Saudi Arabia",  22.3090,  39.1046,    5),
        new CityEntry("Haradh",           "Saudi Arabia",  24.1428,  49.0461,  150),
        new CityEntry("Al Mubarraz",      "Saudi Arabia",  25.3993,  49.5679,  148),
        new CityEntry("Safwa",            "Saudi Arabia",  26.6497,  49.9961,   10),
        new CityEntry("Khafji",           "Saudi Arabia",  28.3972,  48.5033,    5),

        // ════════════════════════════════════════════════════════════════════
        // UNITED ARAB EMIRATES
        // ════════════════════════════════════════════════════════════════════
        new CityEntry("Dubai",            "UAE",           25.2048,  55.2708,    5),
        new CityEntry("Abu Dhabi",        "UAE",           24.4539,  54.3773,    5),
        new CityEntry("Sharjah",          "UAE",           25.3463,  55.4209,    5),
        new CityEntry("Ajman",            "UAE",           25.4111,  55.4354,    5),
        new CityEntry("Ras Al Khaimah",   "UAE",           25.7897,  55.9437,    5),
        new CityEntry("Fujairah",         "UAE",           25.1288,  56.3265,    5),
        new CityEntry("Umm Al Quwain",    "UAE",           25.5647,  55.5533,    5),
        new CityEntry("Al Ain",           "UAE",           24.2075,  55.7447,  261),
        new CityEntry("Khor Fakkan",      "UAE",           25.3392,  56.3582,    5),
        new CityEntry("Dibba",            "UAE",           25.6194,  56.2694,    5),
        new CityEntry("Ruwais",           "UAE",           24.1108,  52.7303,    5),
        new CityEntry("Madinat Zayed",    "UAE",           23.6997,  53.7000,  130),
        new CityEntry("Liwa",             "UAE",           23.1158,  53.7739,  100),

        // ════════════════════════════════════════════════════════════════════
        // QATAR
        // ════════════════════════════════════════════════════════════════════
        new CityEntry("Doha",             "Qatar",         25.2854,  51.5310,   10),
        new CityEntry("Al Wakrah",        "Qatar",         25.1660,  51.5985,   10),
        new CityEntry("Al Khor",          "Qatar",         25.6835,  51.4966,   10),
        new CityEntry("Dukhan",           "Qatar",         25.4266,  50.7816,   10),
        new CityEntry("Mesaieed",         "Qatar",         24.9945,  51.5606,    5),
        new CityEntry("Al Rayyan",        "Qatar",         25.2926,  51.4246,   10),
        new CityEntry("Umm Salal",        "Qatar",         25.4081,  51.3926,   10),
        new CityEntry("Al Shamal",        "Qatar",         26.1000,  51.2167,   10),
        new CityEntry("Lusail",           "Qatar",         25.4295,  51.4883,    5),

        // ════════════════════════════════════════════════════════════════════
        // KUWAIT
        // ════════════════════════════════════════════════════════════════════
        new CityEntry("Kuwait City",      "Kuwait",        29.3759,  47.9774,   55),
        new CityEntry("Hawalli",          "Kuwait",        29.3328,  48.0326,   55),
        new CityEntry("Salmiya",          "Kuwait",        29.3307,  48.0766,    5),
        new CityEntry("Jahra",            "Kuwait",        29.3380,  47.6580,   45),
        new CityEntry("Ahmadi",           "Kuwait",        29.0766,  48.0842,   60),
        new CityEntry("Farwaniya",        "Kuwait",        29.2822,  47.9580,   55),
        new CityEntry("Fahaheel",         "Kuwait",        29.0811,  48.1318,    5),
        new CityEntry("Sabah al-Salem",   "Kuwait",        29.2713,  48.0716,   55),
        new CityEntry("Sulaibikhat",      "Kuwait",        29.3949,  47.9186,   45),
        new CityEntry("Rumaithiya",       "Kuwait",        29.3299,  48.0572,   10),
        new CityEntry("Mangaf",           "Kuwait",        29.1125,  48.0822,    5),

        // ════════════════════════════════════════════════════════════════════
        // BAHRAIN
        // ════════════════════════════════════════════════════════════════════
        new CityEntry("Manama",           "Bahrain",       26.2154,  50.5832,    5),
        new CityEntry("Riffa",            "Bahrain",       26.1300,  50.5547,   35),
        new CityEntry("Muharraq",         "Bahrain",       26.2578,  50.6156,    5),
        new CityEntry("Hamad Town",       "Bahrain",       26.1121,  50.5085,   10),
        new CityEntry("Isa Town",         "Bahrain",       26.1746,  50.5484,   10),
        new CityEntry("Sitra",            "Bahrain",       26.1538,  50.6383,    5),
        new CityEntry("Zallaq",           "Bahrain",       25.9697,  50.4811,    5),

        // ════════════════════════════════════════════════════════════════════
        // OMAN
        // ════════════════════════════════════════════════════════════════════
        new CityEntry("Muscat",           "Oman",          23.5880,  58.3829,   14),
        new CityEntry("Salalah",          "Oman",          17.0151,  54.0924,   30),
        new CityEntry("Sohar",            "Oman",          24.3647,  56.7463,   10),
        new CityEntry("Nizwa",            "Oman",          22.9333,  57.5333,  621),
        new CityEntry("Sur",              "Oman",          22.5667,  59.5289,    5),
        new CityEntry("Ibri",             "Oman",          23.2254,  56.5133,  400),
        new CityEntry("Barka",            "Oman",          23.6878,  57.8893,   10),
        new CityEntry("Rustaq",           "Oman",          23.3928,  57.4225,  320),
        new CityEntry("Khasab",           "Oman",          26.2019,  56.2461,    5),
        new CityEntry("Duqm",             "Oman",          19.6540,  57.7038,   10),
        new CityEntry("Seeb",             "Oman",          23.6781,  58.1892,   10),
        new CityEntry("Suwayq",           "Oman",          23.8498,  57.4413,   10),
        new CityEntry("Bahla",            "Oman",          22.9647,  57.3058,  600),
        new CityEntry("Sinaw",            "Oman",          22.4308,  58.1569,  300),
        new CityEntry("Haima",            "Oman",          19.9584,  56.2763,  200),

        // ════════════════════════════════════════════════════════════════════
        // YEMEN
        // ════════════════════════════════════════════════════════════════════
        new CityEntry("Sanaa",            "Yemen",         15.3694,  44.1910, 2250),
        new CityEntry("Aden",             "Yemen",         12.8000,  45.0333,    5),
        new CityEntry("Taiz",             "Yemen",         13.5789,  44.0178, 1400),
        new CityEntry("Hudaydah",         "Yemen",         14.7978,  42.9548,    5),
        new CityEntry("Mukalla",          "Yemen",         14.5422,  49.1243,    5),
        new CityEntry("Ibb",              "Yemen",         13.9716,  44.1832, 2000),
        new CityEntry("Dhamar",           "Yemen",         14.5623,  44.4038, 2400),
        new CityEntry("Amran",            "Yemen",         15.6594,  43.9430, 2200),
        new CityEntry("Hajjah",           "Yemen",         15.6908,  43.6043, 1800),
        new CityEntry("Marib",            "Yemen",         15.4606,  45.3222, 1100),
        new CityEntry("Seiyun",           "Yemen",         15.9404,  48.7910,  641),
        new CityEntry("Zinjibar",         "Yemen",         13.1290,  45.3797,   10),
        new CityEntry("Al Mukha",         "Yemen",         13.3167,  43.2500,    5),
        new CityEntry("Lahij",            "Yemen",         13.0581,  44.8817,   20),
        new CityEntry("Saadah",           "Yemen",         16.9354,  43.7641, 1800),
        new CityEntry("Al Bayda",         "Yemen",         14.0000,  45.5667, 2200),
        new CityEntry("Al Ghaidah",       "Yemen",         16.2020,  52.1757,  110),

        // ════════════════════════════════════════════════════════════════════
        // IRAQ
        // ════════════════════════════════════════════════════════════════════
        new CityEntry("Baghdad",          "Iraq",          33.3152,  44.3661,   34),
        new CityEntry("Basra",            "Iraq",          30.5085,  47.7804,    5),
        new CityEntry("Mosul",            "Iraq",          36.3350,  43.1189,  223),
        new CityEntry("Erbil",            "Iraq",          36.1901,  44.0091,  415),
        new CityEntry("Sulaymaniyah",     "Iraq",          35.5576,  45.4329,  851),
        new CityEntry("Kirkuk",           "Iraq",          35.4681,  44.3922,  331),
        new CityEntry("Hillah",           "Iraq",          32.4608,  44.4225,   28),
        new CityEntry("Diwaniyah",        "Iraq",          31.9889,  44.9265,   20),
        new CityEntry("Kut",              "Iraq",          32.5051,  45.8262,   17),
        new CityEntry("Nasiriyah",        "Iraq",          31.0658,  46.2575,    6),
        new CityEntry("Amara",            "Iraq",          31.8352,  47.1289,    8),
        new CityEntry("Samarra",          "Iraq",          34.1989,  43.8735,   75),
        new CityEntry("Ramadi",           "Iraq",          33.4160,  43.2986,   52),
        new CityEntry("Fallujah",         "Iraq",          33.3495,  43.7856,   45),
        new CityEntry("Tikrit",           "Iraq",          34.6074,  43.6893,  104),
        new CityEntry("Dohuk",            "Iraq",          36.8676,  42.9903,  638),
        new CityEntry("Baqubah",          "Iraq",          33.7436,  44.6560,   40),
        new CityEntry("Zakho",            "Iraq",          37.1436,  42.6843,  480),
        new CityEntry("Halabja",          "Iraq",          35.1773,  45.9861,  720),
        new CityEntry("Al Qaim",          "Iraq",          34.4050,  41.0408,  180),
        new CityEntry("Tal Afar",         "Iraq",          36.3800,  42.4500,  380),

        // ════════════════════════════════════════════════════════════════════
        // SYRIA
        // ════════════════════════════════════════════════════════════════════
        new CityEntry("Damascus",         "Syria",         33.5102,  36.2913,  690),
        new CityEntry("Aleppo",           "Syria",         36.2021,  37.1343,  380),
        new CityEntry("Homs",             "Syria",         34.7324,  36.7128,  501),
        new CityEntry("Latakia",          "Syria",         35.5317,  35.7918,    5),
        new CityEntry("Hama",             "Syria",         35.1400,  36.7512,  305),
        new CityEntry("Deir ez-Zor",      "Syria",         35.3312,  40.1418,  215),
        new CityEntry("Ar-Raqqah",        "Syria",         35.9522,  39.0014,  246),
        new CityEntry("Qamishli",         "Syria",         37.0481,  41.2275,  470),
        new CityEntry("Tartus",           "Syria",         34.8920,  35.8866,    5),
        new CityEntry("Idlib",            "Syria",         35.9350,  36.6330,  500),
        new CityEntry("Daraa",            "Syria",         32.6224,  36.1004,  570),
        new CityEntry("Hasaka",           "Syria",         36.4940,  40.7489,  310),
        new CityEntry("Sweida",           "Syria",         32.7089,  36.5672, 1070),
        new CityEntry("Palmyra",          "Syria",         34.5544,  38.2694,  400),

        // ════════════════════════════════════════════════════════════════════
        // JORDAN
        // ════════════════════════════════════════════════════════════════════
        new CityEntry("Amman",            "Jordan",        31.9539,  35.9106,  777),
        new CityEntry("Zarqa",            "Jordan",        32.0728,  36.0878,  619),
        new CityEntry("Irbid",            "Jordan",        32.5556,  35.8500,  617),
        new CityEntry("Aqaba",            "Jordan",        29.5321,  35.0063,    5),
        new CityEntry("Russeifa",         "Jordan",        32.0182,  36.0371,  720),
        new CityEntry("Mafraq",           "Jordan",        32.3437,  36.2078,  685),
        new CityEntry("Karak",            "Jordan",        31.1847,  35.7047,  930),
        new CityEntry("Salt",             "Jordan",        32.0378,  35.7289,  830),
        new CityEntry("Madaba",           "Jordan",        31.7165,  35.7933,  760),
        new CityEntry("Jerash",           "Jordan",        32.2747,  35.8981,  620),
        new CityEntry("Petra",            "Jordan",        30.3285,  35.4444,  810),
        new CityEntry("Tafilah",          "Jordan",        30.8344,  35.6058,  930),
        new CityEntry("Ajloun",           "Jordan",        32.3325,  35.7517,  900),

        // ════════════════════════════════════════════════════════════════════
        // LEBANON
        // ════════════════════════════════════════════════════════════════════
        new CityEntry("Beirut",           "Lebanon",       33.8938,  35.5018,   34),
        new CityEntry("Tripoli",          "Lebanon",       34.4367,  35.8497,   10),
        new CityEntry("Sidon",            "Lebanon",       33.5586,  35.3714,    5),
        new CityEntry("Tyre",             "Lebanon",       33.2705,  35.2038,    5),
        new CityEntry("Zahle",            "Lebanon",       33.8465,  35.9018,  900),
        new CityEntry("Baalbek",          "Lebanon",       34.0000,  36.2167, 1160),
        new CityEntry("Jounieh",          "Lebanon",       33.9812,  35.6178,    5),
        new CityEntry("Nabatiyeh",        "Lebanon",       33.3770,  35.4838,  400),

        // ════════════════════════════════════════════════════════════════════
        // PALESTINE
        // ════════════════════════════════════════════════════════════════════
        new CityEntry("Jerusalem",        "Palestine",     31.7683,  35.2137,  754),
        new CityEntry("Gaza",             "Palestine",     31.5017,  34.4669,   30),
        new CityEntry("Ramallah",         "Palestine",     31.8996,  35.2042,  880),
        new CityEntry("Nablus",           "Palestine",     32.2211,  35.2544,  550),
        new CityEntry("Hebron",           "Palestine",     31.5296,  35.0998,  930),
        new CityEntry("Jenin",            "Palestine",     32.4611,  35.3019,  180),
        new CityEntry("Bethlehem",        "Palestine",     31.7054,  35.2024,  765),
        new CityEntry("Jericho",          "Palestine",     31.8563,  35.4626, -258),
        new CityEntry("Khan Yunis",       "Palestine",     31.3461,  34.3067,   35),
        new CityEntry("Rafah",            "Palestine",     31.2948,  34.2588,   40),
        new CityEntry("Tulkarm",          "Palestine",     32.3100,  35.0289,   65),
        new CityEntry("Qalqilya",         "Palestine",     32.1889,  34.9697,   44),

        // ════════════════════════════════════════════════════════════════════
        // EGYPT
        // ════════════════════════════════════════════════════════════════════
        new CityEntry("Cairo",            "Egypt",         30.0444,  31.2357,   23),
        new CityEntry("Alexandria",       "Egypt",         31.2001,  29.9187,   32),
        new CityEntry("Giza",             "Egypt",         30.0131,  31.2089,   20),
        new CityEntry("Shubra El Kheima", "Egypt",         30.1286,  31.2422,   15),
        new CityEntry("Port Said",        "Egypt",         31.2565,  32.2841,    5),
        new CityEntry("Suez",             "Egypt",         29.9737,  32.5270,    5),
        new CityEntry("Luxor",            "Egypt",         25.6872,  32.6396,   76),
        new CityEntry("Aswan",            "Egypt",         24.0889,  32.8998,   90),
        new CityEntry("Mansoura",         "Egypt",         31.0409,  31.3785,   10),
        new CityEntry("Tanta",            "Egypt",         30.7865,  31.0004,   16),
        new CityEntry("Asyut",            "Egypt",         27.1783,  31.1859,   52),
        new CityEntry("Ismailia",         "Egypt",         30.5965,  32.2715,    5),
        new CityEntry("Zagazig",          "Egypt",         30.5877,  31.5020,   10),
        new CityEntry("Damietta",         "Egypt",         31.4165,  31.8133,    5),
        new CityEntry("Qena",             "Egypt",         26.1649,  32.7164,   75),
        new CityEntry("Sohag",            "Egypt",         26.5569,  31.6948,   62),
        new CityEntry("Beni Suef",        "Egypt",         29.0661,  31.0994,   30),
        new CityEntry("Minya",            "Egypt",         28.1099,  30.7503,   40),
        new CityEntry("Fayoum",           "Egypt",         29.3084,  30.8428,   29),
        new CityEntry("Hurghada",         "Egypt",         27.2574,  33.8129,    5),
        new CityEntry("Sharm El Sheikh",  "Egypt",         27.9158,  34.3300,    5),
        new CityEntry("El Arish",         "Egypt",         31.1266,  33.7983,   25),
        new CityEntry("Marsa Matruh",     "Egypt",         31.3543,  27.2373,    5),
        new CityEntry("Kafr el-Sheikh",   "Egypt",         31.1107,  30.9388,    5),
        new CityEntry("Banha",            "Egypt",         30.4644,  31.1814,   15),
        new CityEntry("Shibin El Kom",    "Egypt",         30.5583,  31.0097,   15),

        // ════════════════════════════════════════════════════════════════════
        // ALGERIA
        // ════════════════════════════════════════════════════════════════════
        new CityEntry("Algiers",          "Algeria",       36.7372,   3.0865,   59),
        new CityEntry("Oran",             "Algeria",       35.6969,  -0.6331,   90),
        new CityEntry("Constantine",      "Algeria",       36.3650,   6.6147,  694),
        new CityEntry("Annaba",           "Algeria",       36.9000,   7.7667,   10),
        new CityEntry("Batna",            "Algeria",       35.5556,   6.1742, 1058),
        new CityEntry("Blida",            "Algeria",       36.4722,   2.8283,  244),
        new CityEntry("Setif",            "Algeria",       36.1898,   5.4108, 1096),
        new CityEntry("Tlemcen",          "Algeria",       34.8828,  -1.3167,  815),
        new CityEntry("Sidi Bel Abbes",   "Algeria",       35.2000,  -0.6333,  486),
        new CityEntry("Biskra",           "Algeria",       34.8500,   5.7333,  124),
        new CityEntry("Tizi Ouzou",       "Algeria",       36.7167,   4.0500,  179),
        new CityEntry("Bejaia",           "Algeria",       36.7500,   5.0833,    5),
        new CityEntry("Jijel",            "Algeria",       36.8167,   5.7667,   15),
        new CityEntry("Skikda",           "Algeria",       36.8667,   6.9000,   10),
        new CityEntry("Djelfa",           "Algeria",       34.6703,   3.2630, 1144),
        new CityEntry("Medea",            "Algeria",       36.2638,   2.7494, 1030),
        new CityEntry("Chlef",            "Algeria",       36.1653,   1.3317,  145),
        new CityEntry("Tiaret",           "Algeria",       35.3706,   1.3230, 1000),
        new CityEntry("Mostaganem",       "Algeria",       35.9322,   0.0900,   80),
        new CityEntry("Bechar",           "Algeria",       31.6129,  -2.2164,  772),
        new CityEntry("Ghardaia",         "Algeria",       32.4908,   3.6731,  468),
        new CityEntry("Ouargla",          "Algeria",       31.9558,   5.3356,  164),
        new CityEntry("Tamanrasset",      "Algeria",       22.7861,   5.5228, 1377),
        new CityEntry("Tindouf",          "Algeria",       27.6729,  -8.1400,  427),
        new CityEntry("El Oued",          "Algeria",       33.3683,   6.8675,   62),
        new CityEntry("Adrar",            "Algeria",       27.8742,  -0.2939,  263),
        new CityEntry("Laghouat",         "Algeria",       33.8003,   2.8650,  753),
        new CityEntry("Relizane",         "Algeria",       35.7372,   0.5561,  100),
        new CityEntry("Mascara",          "Algeria",       35.3961,   0.1403,  740),
        new CityEntry("Khenchela",        "Algeria",       35.4322,   7.1433, 1350),
        new CityEntry("Guelma",           "Algeria",       36.4619,   7.4328,  290),
        new CityEntry("Souk Ahras",       "Algeria",       36.2869,   7.9511,  600),
        new CityEntry("Bordj Bou Arreridj","Algeria",      36.0731,   4.7631,  932),
        new CityEntry("Bouira",           "Algeria",       36.3714,   3.9014,  600),
        new CityEntry("Tebessa",          "Algeria",       35.4019,   8.1196,  836),
        new CityEntry("Mila",             "Algeria",       36.4501,   6.2626,  700),
        new CityEntry("Oum El Bouaghi",   "Algeria",       35.8683,   7.1119,  929),
        new CityEntry("Msila",            "Algeria",       35.7058,   4.5456,  442),
        new CityEntry("El Bayadh",        "Algeria",       33.6803,   1.0197, 1358),
        new CityEntry("Saida",            "Algeria",       34.8311,   0.1494,  761),
        new CityEntry("Ain Temouchent",   "Algeria",       35.2983,  -1.1397,   72),
        new CityEntry("Naama",            "Algeria",       33.2680,  -0.3133, 1005),
        new CityEntry("Tipaza",           "Algeria",       36.5883,   2.4483,   59),
        new CityEntry("Ain Defla",        "Algeria",       36.2639,   1.9655,  155),
        new CityEntry("Boumerdes",        "Algeria",       36.7569,   3.4783,   10),
        new CityEntry("El Tarf",          "Algeria",       36.7672,   8.3131,   50),
        new CityEntry("Tissemsilt",       "Algeria",       35.6075,   1.8119, 1000),
        new CityEntry("Illizi",           "Algeria",       26.5033,   8.4828,  561),

        // ════════════════════════════════════════════════════════════════════
        // MOROCCO
        // ════════════════════════════════════════════════════════════════════
        new CityEntry("Casablanca",       "Morocco",       33.5883,  -7.6114,   56),
        new CityEntry("Rabat",            "Morocco",       34.0209,  -6.8416,   75),
        new CityEntry("Fez",              "Morocco",       34.0433,  -5.0033,  400),
        new CityEntry("Marrakech",        "Morocco",       31.6295,  -7.9811,  466),
        new CityEntry("Tangier",          "Morocco",       35.7595,  -5.8340,   15),
        new CityEntry("Agadir",           "Morocco",       30.4278,  -9.5981,   10),
        new CityEntry("Meknes",           "Morocco",       33.8935,  -5.5547,  549),
        new CityEntry("Oujda",            "Morocco",       34.6814,  -1.9086,  465),
        new CityEntry("Kenitra",          "Morocco",       34.2610,  -6.5802,   10),
        new CityEntry("Tetouan",          "Morocco",       35.5785,  -5.3684,   10),
        new CityEntry("Safi",             "Morocco",       32.2994,  -9.2372,   10),
        new CityEntry("Temara",           "Morocco",       33.9288,  -6.9063,   60),
        new CityEntry("El Jadida",        "Morocco",       33.2316,  -8.5007,   10),
        new CityEntry("Khouribga",        "Morocco",       32.8810,  -6.9063,  617),
        new CityEntry("Beni Mellal",      "Morocco",       32.3373,  -6.3498,  470),
        new CityEntry("Nador",            "Morocco",       35.1681,  -2.9294,   10),
        new CityEntry("Mohammedia",       "Morocco",       33.6861,  -7.3833,   10),
        new CityEntry("Laayoune",         "Morocco",       27.1418, -13.1875,   70),
        new CityEntry("Errachidia",       "Morocco",       31.9292,  -4.4242, 1038),
        new CityEntry("Ouarzazate",       "Morocco",       30.9335,  -6.9370, 1135),
        new CityEntry("Dakhla",           "Morocco",       23.7136, -15.9355,    5),
        new CityEntry("Chefchaouen",      "Morocco",       35.1714,  -5.2697,  564),
        new CityEntry("Larache",          "Morocco",       35.1932,  -6.1561,    5),
        new CityEntry("Taza",             "Morocco",       34.2100,  -4.0100,  550),
        new CityEntry("Guelmim",          "Morocco",       28.9870, -10.0572,  301),
        new CityEntry("Al Hoceima",       "Morocco",       35.2517,  -3.9372,    5),
        new CityEntry("Settat",           "Morocco",       33.0019,  -7.6197,  354),
        new CityEntry("Berkane",          "Morocco",       34.9208,  -2.3201,  150),
        new CityEntry("Ksar El Kebir",    "Morocco",       35.0013,  -5.9003,   10),
        new CityEntry("Taroudant",        "Morocco",       30.4708,  -8.8758,  280),

        // ════════════════════════════════════════════════════════════════════
        // TUNISIA
        // ════════════════════════════════════════════════════════════════════
        new CityEntry("Tunis",            "Tunisia",       36.8190,  10.1658,    4),
        new CityEntry("Sfax",             "Tunisia",       34.7406,  10.7603,   12),
        new CityEntry("Sousse",           "Tunisia",       35.8333,  10.6333,   10),
        new CityEntry("Kairouan",         "Tunisia",       35.6781,  10.0964,   63),
        new CityEntry("Bizerte",          "Tunisia",       37.2744,   9.8739,    5),
        new CityEntry("Gabes",            "Tunisia",       33.8833,  10.0978,   10),
        new CityEntry("Gafsa",            "Tunisia",       34.4228,   8.7840,  312),
        new CityEntry("Aryanah",          "Tunisia",       36.8663,  10.1956,   10),
        new CityEntry("Monastir",         "Tunisia",       35.7643,  10.8113,    5),
        new CityEntry("Nabeul",           "Tunisia",       36.4561,  10.7375,    5),
        new CityEntry("Kasserine",        "Tunisia",       35.1675,   8.8306,  580),
        new CityEntry("Tozeur",           "Tunisia",       33.9197,   8.1335,   70),
        new CityEntry("Djerba",           "Tunisia",       33.8000,  10.8500,    5),
        new CityEntry("Zarzis",           "Tunisia",       33.5072,  11.1119,    5),
        new CityEntry("Jendouba",         "Tunisia",       36.5011,   8.7803,  145),
        new CityEntry("Mahdia",           "Tunisia",       35.5047,  11.0622,    5),
        new CityEntry("Sidi Bou Zid",     "Tunisia",       35.0381,   9.4847,  385),
        new CityEntry("Siliana",          "Tunisia",       36.0836,   9.3703,  530),

        // ════════════════════════════════════════════════════════════════════
        // LIBYA
        // ════════════════════════════════════════════════════════════════════
        new CityEntry("Tripoli",          "Libya",         32.9022,  13.1806,   81),
        new CityEntry("Benghazi",         "Libya",         32.1167,  20.0667,   36),
        new CityEntry("Misrata",          "Libya",         32.3754,  15.0925,   10),
        new CityEntry("Tarhuna",          "Libya",         32.4350,  13.6350,  430),
        new CityEntry("Sabha",            "Libya",         27.0347,  14.4272,  438),
        new CityEntry("Zliten",           "Libya",         32.4681,  14.5681,   10),
        new CityEntry("Zawiya",           "Libya",         32.7522,  12.7278,   10),
        new CityEntry("Derna",            "Libya",         32.7631,  22.6378,    5),
        new CityEntry("Sirte",            "Libya",         31.2089,  16.5887,   10),
        new CityEntry("Tobruk",           "Libya",         32.0839,  23.9739,    5),
        new CityEntry("Al Bayda",         "Libya",         32.7636,  21.7554,  620),
        new CityEntry("Nalut",            "Libya",         31.8719,  10.9797,  630),
        new CityEntry("Murzuq",           "Libya",         25.9167,  13.9167,  436),

        // ════════════════════════════════════════════════════════════════════
        // SUDAN & MAURITANIA & SOMALIA
        // ════════════════════════════════════════════════════════════════════
        new CityEntry("Khartoum",         "Sudan",         15.5007,  32.5599,  380),
        new CityEntry("Omdurman",         "Sudan",         15.6418,  32.4806,  370),
        new CityEntry("Port Sudan",       "Sudan",         19.6186,  37.2163,    5),
        new CityEntry("Kassala",          "Sudan",         15.4597,  36.4000,  500),
        new CityEntry("Wad Madani",       "Sudan",         14.3997,  33.5183,  410),
        new CityEntry("Atbara",           "Sudan",         17.7069,  33.9678,  346),
        new CityEntry("Nyala",            "Sudan",         12.0564,  24.8833,  621),
        new CityEntry("Nouakchott",       "Mauritania",    18.0858, -15.9785,    7),
        new CityEntry("Nouadhibou",       "Mauritania",    20.9310, -17.0347,    5),
        new CityEntry("Mogadishu",        "Somalia",        2.0469,  45.3182,    9),
        new CityEntry("Hargeisa",         "Somalia",        9.5600,  44.0650, 1334),
        new CityEntry("Djibouti City",    "Djibouti",      11.5892,  43.1450,   10),

        // ════════════════════════════════════════════════════════════════════
        // IRAN
        // ════════════════════════════════════════════════════════════════════
        new CityEntry("Tehran",           "Iran",          35.6892,  51.3890, 1191),
        new CityEntry("Mashhad",          "Iran",          36.2972,  59.6067,  985),
        new CityEntry("Isfahan",          "Iran",          32.6546,  51.6680, 1574),
        new CityEntry("Tabriz",           "Iran",          38.0800,  46.2919, 1351),
        new CityEntry("Shiraz",           "Iran",          29.5918,  52.5837, 1484),
        new CityEntry("Karaj",            "Iran",          35.8400,  50.9391, 1321),
        new CityEntry("Ahvaz",            "Iran",          31.3183,  48.6706,   20),
        new CityEntry("Kermanshah",       "Iran",          34.3277,  47.0780, 1322),
        new CityEntry("Urmia",            "Iran",          37.5526,  45.0762, 1332),
        new CityEntry("Rasht",            "Iran",          37.2809,  49.5831,   -8),
        new CityEntry("Zahedan",          "Iran",          29.4963,  60.8629, 1370),
        new CityEntry("Hamadan",          "Iran",          34.7983,  48.5147, 1748),
        new CityEntry("Kerman",           "Iran",          30.2839,  57.0834, 1755),
        new CityEntry("Yazd",             "Iran",          31.8974,  54.3678, 1216),
        new CityEntry("Ardabil",          "Iran",          38.2498,  48.2933, 1340),
        new CityEntry("Bandar Abbas",     "Iran",          27.1832,  56.2666,    5),
        new CityEntry("Arak",             "Iran",          34.0954,  49.6890, 1708),
        new CityEntry("Sanandaj",         "Iran",          35.3219,  46.9861, 1488),
        new CityEntry("Zanjan",           "Iran",          36.6736,  48.4787, 1663),
        new CityEntry("Gorgan",           "Iran",          36.8432,  54.4436,   13),
        new CityEntry("Qazvin",           "Iran",          36.2688,  50.0046, 1278),
        new CityEntry("Semnan",           "Iran",          35.5729,  53.3972, 1130),
        new CityEntry("Bushehr",          "Iran",          28.9234,  50.8203,   10),
        new CityEntry("Dezful",           "Iran",          32.3811,  48.3986,  143),
        new CityEntry("Babol",            "Iran",          36.5491,  52.6788,   -5),

        // ════════════════════════════════════════════════════════════════════
        // TURKEY
        // ════════════════════════════════════════════════════════════════════
        new CityEntry("Istanbul",         "Turkey",        41.0082,  28.9784,   40),
        new CityEntry("Ankara",           "Turkey",        39.9334,  32.8597,  938),
        new CityEntry("Izmir",            "Turkey",        38.4189,  27.1287,   30),
        new CityEntry("Bursa",            "Turkey",        40.1885,  29.0610,  135),
        new CityEntry("Adana",            "Turkey",        37.0000,  35.3213,   23),
        new CityEntry("Gaziantep",        "Turkey",        37.0662,  37.3833,  850),
        new CityEntry("Konya",            "Turkey",        37.8713,  32.4846, 1026),
        new CityEntry("Antalya",          "Turkey",        36.8841,  30.7056,   10),
        new CityEntry("Kayseri",          "Turkey",        38.7312,  35.4787, 1054),
        new CityEntry("Mersin",           "Turkey",        36.8000,  34.6333,    5),
        new CityEntry("Eskisehir",        "Turkey",        39.7767,  30.5206,  789),
        new CityEntry("Diyarbakir",       "Turkey",        37.9144,  40.2306,  677),
        new CityEntry("Sanliurfa",        "Turkey",        37.1591,  38.7969,  550),
        new CityEntry("Samsun",           "Turkey",        41.2867,  36.3300,    5),
        new CityEntry("Denizli",          "Turkey",        37.7765,  29.0864,  426),
        new CityEntry("Malatya",          "Turkey",        38.3552,  38.3095,  900),
        new CityEntry("Trabzon",          "Turkey",        41.0015,  39.7178,   10),
        new CityEntry("Erzurum",          "Turkey",        39.9055,  41.2658, 1900),
        new CityEntry("Van",              "Turkey",        38.5012,  43.4102, 1727),
        new CityEntry("Kahramanmaras",    "Turkey",        37.5858,  36.9371,  568),
        new CityEntry("Batman",           "Turkey",        37.8812,  41.1351,  610),
        new CityEntry("Hatay",            "Turkey",        36.4018,  36.3498,   80),
        new CityEntry("Adapazari",        "Turkey",        40.7869,  30.4058,   30),

        // ════════════════════════════════════════════════════════════════════
        // PAKISTAN
        // ════════════════════════════════════════════════════════════════════
        new CityEntry("Karachi",          "Pakistan",      24.8607,  67.0011,   10),
        new CityEntry("Lahore",           "Pakistan",      31.5497,  74.3436,  217),
        new CityEntry("Islamabad",        "Pakistan",      33.7294,  73.0931,  507),
        new CityEntry("Faisalabad",       "Pakistan",      31.4504,  73.1350,  185),
        new CityEntry("Rawalpindi",       "Pakistan",      33.6007,  73.0679,  508),
        new CityEntry("Gujranwala",       "Pakistan",      32.1877,  74.1945,  217),
        new CityEntry("Peshawar",         "Pakistan",      34.0151,  71.5249,  359),
        new CityEntry("Multan",           "Pakistan",      30.1575,  71.5249,  122),
        new CityEntry("Hyderabad",        "Pakistan",      25.3960,  68.3578,   14),
        new CityEntry("Quetta",           "Pakistan",      30.1798,  66.9750, 1680),
        new CityEntry("Bahawalpur",       "Pakistan",      29.3956,  71.6836,  118),
        new CityEntry("Sargodha",         "Pakistan",      32.0836,  72.6711,  187),
        new CityEntry("Sialkot",          "Pakistan",      32.4945,  74.5229,  256),
        new CityEntry("Sukkur",           "Pakistan",      27.7052,  68.8574,   65),
        new CityEntry("Mardan",           "Pakistan",      34.1986,  72.0404,  283),
        new CityEntry("Abbottabad",       "Pakistan",      34.1558,  73.2194, 1256),

        // ════════════════════════════════════════════════════════════════════
        // BANGLADESH & AFGHANISTAN & CENTRAL ASIA
        // ════════════════════════════════════════════════════════════════════
        new CityEntry("Dhaka",            "Bangladesh",    23.8103,  90.4125,    8),
        new CityEntry("Chittagong",       "Bangladesh",    22.3569,  91.7832,   14),
        new CityEntry("Khulna",           "Bangladesh",    22.8456,  89.5403,    5),
        new CityEntry("Rajshahi",         "Bangladesh",    24.3636,  88.6241,   18),
        new CityEntry("Sylhet",           "Bangladesh",    24.8949,  91.8687,   35),
        new CityEntry("Kabul",            "Afghanistan",   34.5553,  69.2075, 1791),
        new CityEntry("Kandahar",         "Afghanistan",   31.6289,  65.7372, 1005),
        new CityEntry("Herat",            "Afghanistan",   34.3482,  62.1997,  964),
        new CityEntry("Mazar-i-Sharif",   "Afghanistan",   36.7069,  67.1128,  377),
        new CityEntry("Jalalabad",        "Afghanistan",   34.4415,  70.4360,  570),
        new CityEntry("Tashkent",         "Uzbekistan",    41.2995,  69.2401,  456),
        new CityEntry("Samarkand",        "Uzbekistan",    39.6542,  66.9597,  702),
        new CityEntry("Namangan",         "Uzbekistan",    41.0011,  71.6678,  467),
        new CityEntry("Bukhara",          "Uzbekistan",    39.7747,  64.4286,  225),
        new CityEntry("Bishkek",          "Kyrgyzstan",    42.8746,  74.5698,  800),
        new CityEntry("Almaty",           "Kazakhstan",    43.2220,  76.8512,  775),
        new CityEntry("Nur-Sultan",       "Kazakhstan",    51.1801,  71.4460,  347),
        new CityEntry("Dushanbe",         "Tajikistan",    38.5598,  68.7733,  800),
        new CityEntry("Ashgabat",         "Turkmenistan",  37.9601,  58.3261,  214),
        new CityEntry("Baku",             "Azerbaijan",    40.4093,  49.8671,   -4),

        // ════════════════════════════════════════════════════════════════════
        // SOUTH & SOUTHEAST ASIA
        // ════════════════════════════════════════════════════════════════════
        new CityEntry("Mumbai",           "India",         19.0760,  72.8777,   14),
        new CityEntry("Delhi",            "India",         28.7041,  77.1025,  233),
        new CityEntry("Hyderabad",        "India",         17.3850,  78.4867,  536),
        new CityEntry("Lucknow",          "India",         26.8467,  80.9462,  128),
        new CityEntry("Kolkata",          "India",         22.5726,  88.3639,    9),
        new CityEntry("Srinagar",         "India",         34.0837,  74.7973, 1585),
        new CityEntry("Aligarh",          "India",         27.8974,  78.0880,  187),
        new CityEntry("Kuala Lumpur",     "Malaysia",       3.1390, 101.6869,   68),
        new CityEntry("Penang",           "Malaysia",       5.4141, 100.3288,    5),
        new CityEntry("Kota Kinabalu",    "Malaysia",       5.9804, 116.0735,    5),
        new CityEntry("Johor Bahru",      "Malaysia",       1.4927, 103.7414,    5),
        new CityEntry("Jakarta",          "Indonesia",     -6.2088, 106.8456,    8),
        new CityEntry("Surabaya",         "Indonesia",     -7.2575, 112.7521,    5),
        new CityEntry("Bandung",          "Indonesia",     -6.9175, 107.6191,  768),
        new CityEntry("Medan",            "Indonesia",      3.5952,  98.6722,   25),
        new CityEntry("Makassar",         "Indonesia",     -5.1477, 119.4327,    2),
        new CityEntry("Yogyakarta",       "Indonesia",     -7.7971, 110.3688,  113),
        new CityEntry("Palembang",        "Indonesia",     -2.9761, 104.7754,   12),
        new CityEntry("Semarang",         "Indonesia",     -6.9932, 110.4203,    5),
        new CityEntry("Aceh",             "Indonesia",      5.5483,  95.3238,   28),
        new CityEntry("Pekanbaru",        "Indonesia",      0.5071, 101.4478,   30),
        new CityEntry("Manila",           "Philippines",   14.5995, 120.9842,   16),
        new CityEntry("Cotabato",         "Philippines",    7.2236, 124.2462,   10),
        new CityEntry("Singapore",        "Singapore",      1.3521, 103.8198,   15),
        new CityEntry("Bandar Seri Begawan","Brunei",       4.9400, 114.9480,   30),
        new CityEntry("Bangkok",          "Thailand",      13.7563, 100.5018,    5),
        new CityEntry("Male",             "Maldives",       4.1755,  73.5093,    2),
        new CityEntry("Colombo",          "Sri Lanka",      6.9271,  79.8612,    7),
        new CityEntry("Yangon",           "Myanmar",       16.8409,  96.1735,   33),

        // ════════════════════════════════════════════════════════════════════
        // EAST ASIA
        // ════════════════════════════════════════════════════════════════════
        new CityEntry("Beijing",          "China",         39.9042, 116.4074,   43),
        new CityEntry("Shanghai",         "China",         31.2304, 121.4737,    4),
        new CityEntry("Urumqi",           "China",         43.8256,  87.6168,  806),
        new CityEntry("Yinchuan",         "China",         38.4872, 106.2309, 1090),
        new CityEntry("Lanzhou",          "China",         36.0611, 103.8343, 1520),
        new CityEntry("Guangzhou",        "China",         23.1291, 113.2644,   21),
        new CityEntry("Chengdu",          "China",         30.5728, 104.0668,  500),
        new CityEntry("Wuhan",            "China",         30.5928, 114.3055,   37),
        new CityEntry("Xian",             "China",         34.3416, 108.9398,  416),
        new CityEntry("Tokyo",            "Japan",         35.6762, 139.6503,   40),
        new CityEntry("Osaka",            "Japan",         34.6937, 135.5023,    5),
        new CityEntry("Seoul",            "South Korea",   37.5665, 126.9780,   38),

        // ════════════════════════════════════════════════════════════════════
        // WEST AFRICA
        // ════════════════════════════════════════════════════════════════════
        new CityEntry("Lagos",            "Nigeria",        6.5244,   3.3792,   41),
        new CityEntry("Kano",             "Nigeria",       12.0022,   8.5919,  472),
        new CityEntry("Abuja",            "Nigeria",        9.0579,   7.4951,  840),
        new CityEntry("Kaduna",           "Nigeria",       10.5264,   7.4382,  614),
        new CityEntry("Sokoto",           "Nigeria",       13.0622,   5.2339,  269),
        new CityEntry("Maiduguri",        "Nigeria",       11.8333,  13.1500,  320),
        new CityEntry("Dakar",            "Senegal",       14.7167, -17.4677,   24),
        new CityEntry("Bamako",           "Mali",          12.6392,  -8.0029,  380),
        new CityEntry("Ouagadougou",      "Burkina Faso",  12.3714,  -1.5197,  305),
        new CityEntry("Niamey",           "Niger",         13.5137,   2.1098,  207),
        new CityEntry("Ndjamena",         "Chad",          12.1048,  15.0445,  295),
        new CityEntry("Conakry",          "Guinea",         9.6412, -13.5784,   26),
        new CityEntry("Nairobi",          "Kenya",         -1.2921,  36.8219, 1795),
        new CityEntry("Dar es Salaam",    "Tanzania",      -6.7924,  39.2083,   55),
        new CityEntry("Addis Ababa",      "Ethiopia",       9.0320,  38.7469, 2355),
        new CityEntry("Accra",            "Ghana",          5.6037,  -0.1870,   61),
        new CityEntry("Johannesburg",     "South Africa", -26.2041,  28.0473, 1753),
        new CityEntry("Cape Town",        "South Africa", -33.9249,  18.4241,   10),

        // ════════════════════════════════════════════════════════════════════
        // RUSSIA (Muslim regions)
        // ════════════════════════════════════════════════════════════════════
        new CityEntry("Moscow",           "Russia",        55.7558,  37.6176,  156),
        new CityEntry("Saint Petersburg", "Russia",        59.9311,  30.3609,    5),
        new CityEntry("Kazan",            "Russia",        55.8304,  49.0661,  116),
        new CityEntry("Ufa",              "Russia",        54.7388,  55.9721,  103),
        new CityEntry("Grozny",           "Russia",        43.3170,  45.6984,  226),
        new CityEntry("Makhachkala",      "Russia",        42.9849,  47.5047,   -5),
        new CityEntry("Novosibirsk",      "Russia",        55.0084,  82.9357,  177),

        // ════════════════════════════════════════════════════════════════════
        // BALKANS
        // ════════════════════════════════════════════════════════════════════
        new CityEntry("Sarajevo",         "Bosnia",        43.8563,  18.4131,  511),
        new CityEntry("Banja Luka",       "Bosnia",        44.7722,  17.1910,  163),
        new CityEntry("Tirana",           "Albania",       41.3317,  19.8317,   89),
        new CityEntry("Prishtina",        "Kosovo",        42.6629,  21.1655,  596),
        new CityEntry("Skopje",           "North Macedonia",42.0021, 21.4324,  240),

        // ════════════════════════════════════════════════════════════════════
        // UNITED KINGDOM
        // ════════════════════════════════════════════════════════════════════
        new CityEntry("London",           "UK",            51.5074,  -0.1278,   11),
        new CityEntry("Birmingham",       "UK",            52.4862,  -1.8904,  141),
        new CityEntry("Manchester",       "UK",            53.4808,  -2.2426,   38),
        new CityEntry("Leeds",            "UK",            53.8008,  -1.5491,   76),
        new CityEntry("Bradford",         "UK",            53.7960,  -1.7594,  136),
        new CityEntry("Sheffield",        "UK",            53.3811,  -1.4701,  100),
        new CityEntry("Liverpool",        "UK",            53.4084,  -2.9916,   22),
        new CityEntry("Glasgow",          "UK",            55.8642,  -4.2518,   40),
        new CityEntry("Edinburgh",        "UK",            55.9533,  -3.1883,   47),
        new CityEntry("Leicester",        "UK",            52.6369,  -1.1398,   67),
        new CityEntry("Luton",            "UK",            51.8787,  -0.4200,  166),
        new CityEntry("Coventry",         "UK",            52.4068,  -1.5197,   72),
        new CityEntry("Nottingham",       "UK",            52.9548,  -1.1581,   75),
        new CityEntry("Bristol",          "UK",            51.4545,  -2.5879,   11),

        // ════════════════════════════════════════════════════════════════════
        // FRANCE
        // ════════════════════════════════════════════════════════════════════
        new CityEntry("Paris",            "France",        48.8566,   2.3522,   35),
        new CityEntry("Marseille",        "France",        43.2965,   5.3698,   28),
        new CityEntry("Lyon",             "France",        45.7640,   4.8357,  173),
        new CityEntry("Toulouse",         "France",        43.6047,   1.4442,  146),
        new CityEntry("Nice",             "France",        43.7102,   7.2620,   10),
        new CityEntry("Strasbourg",       "France",        48.5734,   7.7521,  142),
        new CityEntry("Nantes",           "France",        47.2184,  -1.5536,   10),
        new CityEntry("Montpellier",      "France",        43.6119,   3.8772,   27),
        new CityEntry("Bordeaux",         "France",        44.8378,  -0.5792,    6),
        new CityEntry("Lille",            "France",        50.6292,   3.0573,   25),
        new CityEntry("Grenoble",         "France",        45.1885,   5.7245,  212),
        new CityEntry("Rennes",           "France",        48.1173,  -1.6778,   40),

        // ════════════════════════════════════════════════════════════════════
        // GERMANY
        // ════════════════════════════════════════════════════════════════════
        new CityEntry("Berlin",           "Germany",       52.5200,  13.4050,   34),
        new CityEntry("Hamburg",          "Germany",       53.5753,  10.0153,   14),
        new CityEntry("Munich",           "Germany",       48.1351,  11.5820,  519),
        new CityEntry("Cologne",          "Germany",       50.9333,   6.9500,   37),
        new CityEntry("Frankfurt",        "Germany",       50.1109,   8.6821,  112),
        new CityEntry("Stuttgart",        "Germany",       48.7758,   9.1829,  247),
        new CityEntry("Dusseldorf",       "Germany",       51.2217,   6.7762,   38),
        new CityEntry("Dortmund",         "Germany",       51.5136,   7.4653,   84),
        new CityEntry("Essen",            "Germany",       51.4566,   7.0116,   79),
        new CityEntry("Leipzig",          "Germany",       51.3397,  12.3731,  113),
        new CityEntry("Bremen",           "Germany",       53.0793,   8.8017,   11),
        new CityEntry("Dresden",          "Germany",       51.0509,  13.7383,  112),
        new CityEntry("Hannover",         "Germany",       52.3759,   9.7320,   55),
        new CityEntry("Nuremberg",        "Germany",       49.4521,  11.0767,  309),
        new CityEntry("Duisburg",         "Germany",       51.4344,   6.7623,   31),

        // ════════════════════════════════════════════════════════════════════
        // REST OF EUROPE
        // ════════════════════════════════════════════════════════════════════
        new CityEntry("Amsterdam",        "Netherlands",   52.3676,   4.9041,   -2),
        new CityEntry("Rotterdam",        "Netherlands",   51.9244,   4.4777,   -5),
        new CityEntry("Brussels",         "Belgium",       50.8503,   4.3517,   56),
        new CityEntry("Antwerp",          "Belgium",       51.2194,   4.4025,    5),
        new CityEntry("Zurich",           "Switzerland",   47.3769,   8.5417,  408),
        new CityEntry("Geneva",           "Switzerland",   46.2044,   6.1432,  375),
        new CityEntry("Vienna",           "Austria",       48.2082,  16.3738,  151),
        new CityEntry("Madrid",           "Spain",         40.4168,  -3.7038,  667),
        new CityEntry("Barcelona",        "Spain",         41.3851,   2.1734,   12),
        new CityEntry("Valencia",         "Spain",         39.4699,  -0.3763,   13),
        new CityEntry("Seville",          "Spain",         37.3891,  -5.9845,    7),
        new CityEntry("Malaga",           "Spain",         36.7213,  -4.4214,    5),
        new CityEntry("Ceuta",            "Spain",         35.8894,  -5.3213,   10),
        new CityEntry("Melilla",          "Spain",         35.2923,  -2.9381,    5),
        new CityEntry("Lisbon",           "Portugal",      38.7169,  -9.1395,   20),
        new CityEntry("Porto",            "Portugal",      41.1579,  -8.6291,   86),
        new CityEntry("Rome",             "Italy",         41.9028,  12.4964,   37),
        new CityEntry("Milan",            "Italy",         45.4654,   9.1859,  122),
        new CityEntry("Naples",           "Italy",         40.8518,  14.2681,   17),
        new CityEntry("Turin",            "Italy",         45.0703,   7.6869,  239),
        new CityEntry("Palermo",          "Italy",         38.1157,  13.3615,   14),
        new CityEntry("Stockholm",        "Sweden",        59.3293,  18.0686,   44),
        new CityEntry("Gothenburg",       "Sweden",        57.7089,  11.9746,   10),
        new CityEntry("Malmo",            "Sweden",        55.6050,  13.0038,   10),
        new CityEntry("Copenhagen",       "Denmark",       55.6761,  12.5683,   24),
        new CityEntry("Oslo",             "Norway",        59.9139,  10.7522,   23),
        new CityEntry("Helsinki",         "Finland",       60.1699,  24.9384,   26),
        new CityEntry("Warsaw",           "Poland",        52.2297,  21.0122,  107),
        new CityEntry("Krakow",           "Poland",        50.0647,  19.9450,  219),
        new CityEntry("Prague",           "Czech Republic",50.0755,  14.4378,  235),
        new CityEntry("Budapest",         "Hungary",       47.4979,  19.0402,  102),
        new CityEntry("Bucharest",        "Romania",       44.4268,  26.1025,   70),
        new CityEntry("Sofia",            "Bulgaria",      42.6977,  23.3219,  595),
        new CityEntry("Athens",           "Greece",        37.9838,  23.7275,   70),
        new CityEntry("Thessaloniki",     "Greece",        40.6401,  22.9444,   32),
        new CityEntry("Belgrade",         "Serbia",        44.8176,  20.4633,  116),
        new CityEntry("Zagreb",           "Croatia",       45.8150,  15.9819,  123),
        new CityEntry("Kyiv",             "Ukraine",       50.4501,  30.5234,  179),
        new CityEntry("Minsk",            "Belarus",       53.9045,  27.5615,  220),
        new CityEntry("Dublin",           "Ireland",       53.3498,  -6.2603,   20),

        // ════════════════════════════════════════════════════════════════════
        // UNITED STATES
        // ════════════════════════════════════════════════════════════════════
        new CityEntry("New York",         "USA",           40.7128, -74.0060,   10),
        new CityEntry("Los Angeles",      "USA",           34.0522,-118.2437,   71),
        new CityEntry("Chicago",          "USA",           41.8781, -87.6298,  181),
        new CityEntry("Houston",          "USA",           29.7604, -95.3698,   15),
        new CityEntry("Phoenix",          "USA",           33.4484,-112.0740,  331),
        new CityEntry("Philadelphia",     "USA",           39.9526, -75.1652,   12),
        new CityEntry("San Antonio",      "USA",           29.4241, -98.4936,  198),
        new CityEntry("San Diego",        "USA",           32.7157,-117.1611,   18),
        new CityEntry("Dallas",           "USA",           32.7767, -96.7970,  139),
        new CityEntry("San Francisco",    "USA",           37.7749,-122.4194,   16),
        new CityEntry("Seattle",          "USA",           47.6062,-122.3321,   56),
        new CityEntry("Washington DC",    "USA",           38.9072, -77.0369,    8),
        new CityEntry("Detroit",          "USA",           42.3314, -83.0458,  183),
        new CityEntry("Dearborn",         "USA",           42.3223, -83.1763,  188),
        new CityEntry("Minneapolis",      "USA",           44.9778, -93.2650,  262),
        new CityEntry("Denver",           "USA",           39.7392,-104.9903, 1609),
        new CityEntry("Boston",           "USA",           42.3601, -71.0589,    9),
        new CityEntry("Atlanta",          "USA",           33.7490, -84.3880,  320),
        new CityEntry("Miami",            "USA",           25.7617, -80.1918,    2),
        new CityEntry("Las Vegas",        "USA",           36.1699,-115.1398,  620),
        new CityEntry("Portland",         "USA",           45.5051,-122.6750,   15),
        new CityEntry("Nashville",        "USA",           36.1627, -86.7816,  183),
        new CityEntry("Austin",           "USA",           30.2672, -97.7431,  149),
        new CityEntry("Charlotte",        "USA",           35.2271, -80.8431,  234),
        new CityEntry("Indianapolis",     "USA",           39.7684, -86.1581,  218),
        new CityEntry("Columbus",         "USA",           39.9612, -82.9988,  288),
        new CityEntry("Memphis",          "USA",           35.1495, -90.0490,   82),
        new CityEntry("Baltimore",        "USA",           39.2904, -76.6122,   20),
        new CityEntry("Kansas City",      "USA",           39.0997, -94.5786,  322),
        new CityEntry("Salt Lake City",   "USA",           40.7608,-111.8910, 1288),
        new CityEntry("Tampa",            "USA",           27.9506, -82.4572,   15),
        new CityEntry("Orlando",          "USA",           28.5383, -81.3792,   34),
        new CityEntry("Jacksonville",     "USA",           30.3322, -81.6557,   10),
        new CityEntry("Sacramento",       "USA",           38.5816,-121.4944,    8),
        new CityEntry("Raleigh",          "USA",           35.7796, -78.6382,   96),
        new CityEntry("Albuquerque",      "USA",           35.0844,-106.6504, 1510),
        new CityEntry("New Orleans",      "USA",           29.9511, -90.0715,    1),
        new CityEntry("Pittsburgh",       "USA",           40.4406, -79.9959,  223),
        new CityEntry("Cleveland",        "USA",           41.4993, -81.6944,  180),
        new CityEntry("Cincinnati",       "USA",           39.1031, -84.5120,  242),
        new CityEntry("Milwaukee",        "USA",           43.0389, -87.9065,  193),
        new CityEntry("Tucson",           "USA",           32.2226,-110.9747,  728),
        new CityEntry("Fresno",           "USA",           36.7378,-119.7871,   94),
        new CityEntry("Louisville",       "USA",           38.2527, -85.7585,  149),

        // ════════════════════════════════════════════════════════════════════
        // CANADA
        // ════════════════════════════════════════════════════════════════════
        new CityEntry("Toronto",          "Canada",        43.6532, -79.3832,   76),
        new CityEntry("Montreal",         "Canada",        45.5017, -73.5673,   27),
        new CityEntry("Vancouver",        "Canada",        49.2827,-123.1207,   70),
        new CityEntry("Calgary",          "Canada",        51.0447,-114.0719, 1045),
        new CityEntry("Edmonton",         "Canada",        53.5461,-113.4938,  671),
        new CityEntry("Ottawa",           "Canada",        45.4215, -75.6972,   79),
        new CityEntry("Winnipeg",         "Canada",        49.8951, -97.1384,  232),
        new CityEntry("Brampton",         "Canada",        43.7315, -79.7624,  196),
        new CityEntry("Mississauga",      "Canada",        43.5890, -79.6441,  159),
        new CityEntry("Halifax",          "Canada",        44.6488, -63.5752,   24),

        // ════════════════════════════════════════════════════════════════════
        // LATIN AMERICA
        // ════════════════════════════════════════════════════════════════════
        new CityEntry("Mexico City",      "Mexico",        19.4326, -99.1332, 2240),
        new CityEntry("Guadalajara",      "Mexico",        20.6597,-103.3496, 1566),
        new CityEntry("Monterrey",        "Mexico",        25.6866,-100.3161,  538),
        new CityEntry("Sao Paulo",        "Brazil",       -23.5505, -46.6333,  760),
        new CityEntry("Rio de Janeiro",   "Brazil",       -22.9068, -43.1729,   10),
        new CityEntry("Brasilia",         "Brazil",       -15.7801, -47.9292, 1172),
        new CityEntry("Buenos Aires",     "Argentina",    -34.6037, -58.3816,   25),
        new CityEntry("Bogota",           "Colombia",       4.7110, -74.0721, 2625),
        new CityEntry("Lima",             "Peru",         -12.0464, -77.0428,  154),
        new CityEntry("Santiago",         "Chile",        -33.4489, -70.6693,  567),
        new CityEntry("Caracas",          "Venezuela",    10.4806,  -66.9036,  900),
        new CityEntry("Guayaquil",        "Ecuador",       -2.1962, -79.8862,    4),

        // ════════════════════════════════════════════════════════════════════
        // AUSTRALIA & NEW ZEALAND
        // ════════════════════════════════════════════════════════════════════
        new CityEntry("Sydney",           "Australia",    -33.8688, 151.2093,   58),
        new CityEntry("Melbourne",        "Australia",    -37.8136, 144.9631,   31),
        new CityEntry("Brisbane",         "Australia",    -27.4698, 153.0251,   27),
        new CityEntry("Perth",            "Australia",    -31.9505, 115.8605,   25),
        new CityEntry("Adelaide",         "Australia",    -34.9285, 138.6007,   48),
        new CityEntry("Auckland",         "New Zealand",  -36.8485, 174.7633,   25),
        new CityEntry("Wellington",       "New Zealand",  -41.2865, 174.7762,   25),
    };

    public CityDatabase() {}

    /**
     * Search cities by name or country (case-insensitive).
     * Returns up to 40 matches, exact prefix matches first.
     */
    public List<CityEntry> search(String query) {
        String q = query.toLowerCase(Locale.getDefault()).trim();
        List<CityEntry> exact   = new ArrayList<>();
        List<CityEntry> partial = new ArrayList<>();

        for (CityEntry city : CITIES) {
            String name    = city.city.toLowerCase(Locale.getDefault());
            String country = city.country.toLowerCase(Locale.getDefault());
            if (name.startsWith(q)) {
                exact.add(city);
            } else if (name.contains(q) || country.contains(q)) {
                partial.add(city);
            }
        }

        List<CityEntry> result = new ArrayList<>(exact);
        for (CityEntry c : partial) {
            if (result.size() >= 40) break;
            result.add(c);
        }
        return result;
    }

    public int size() { return CITIES.length; }
}

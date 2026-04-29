# 🕌 وقتك — Waqtuk

<div align="center">

**تطبيق مواقيت الصلاة | Islamic Prayer Times App**

🌙 *دقيق · بدون إنترنت · عربي أولاً* 🌙

*Accurate · Offline · Arabic First*

</div>

---

## ar بالعربية

### 🕌 ما هو وقتك؟
**وقتك** تطبيق أندرويد مجاني ومفتوح المصدر لمواقيت الصلاة.
يعمل بدون إنترنت بعد أول تشغيل، ويدعم أكثر من **1500 مدينة** حول العالم.

---

### ✨ المميزات

| الميزة | الوصف |
|--------|-------|
| 🔭 **مواقيت دقيقة** | حساب فلكي محلي بدون إنترنت |
| 🌐 **معايرة تلقائية** | يتصل بـ aladhan.com مرة واحدة ليضبط الفرق حسب بلدك |
| 📍 **GPS تلقائي** | يحدد موقعك فوراً بدون تأخير |
| 🏙️ **بحث عن مدينة** | أكثر من 1500 مدينة بدون إنترنت |
| 📢 **أذان حقيقي** | مع إشعار كامل وزر إيقاف فوري |
| 🕐 **وقت الإقامة** | الفجر +17 دقيقة، المغرب +7، الباقي +10 |
| 🕌 **صلاة الجمعة** | بطاقة خاصة تظهر تلقائياً كل يوم جمعة |
| 📅 **التاريخ الهجري** | يحسب ويعرض تلقائياً |
| 🌙 **واجهة عربية كاملة** | RTL + عربي كلغة افتراضية |
| ⚙️ **تعديل المواقيت** | + أو − دقائق لكل صلاة يدوياً |
| 🔄 **استمرارية بعد الإقلاع** | يعيد جدولة الأذان تلقائياً بعد إعادة التشغيل |

---

### 🌍 طرق الحساب المدعومة

| المنطقة | الطريقة |
|---------|---------|
| 🇩🇿 الجزائر، المغرب، تونس، ليبيا، مصر | الهيئة المصرية العامة للمساحة |
| 🇸🇦 السعودية، الخليج، اليمن | أم القرى — مكة المكرمة |
| 🇵🇰 باكستان، أفغانستان، الهند | جامعة كراتشي |
| 🇮🇷 إيران | معهد الجيوفيزياء — طهران |
| 🇺🇸 أمريكا الشمالية | ISNA |
| 🌐 أوروبا والباقي | رابطة العالم الإسلامي |

---

### 🕋 أوقات الصلاة

| الصلاة | الرمز |
|--------|-------|
| 🌅 الفجر | قبل شروق الشمس |
| ☀️ الشروق | عند بزوغ الشمس |
| 🌞 الظهر | منتصف النهار الشمسي |
| ⛅ العصر | بعد الزوال |
| 🌇 المغرب | عند غروب الشمس |
| 🌙 العشاء | بعد اختفاء الشفق |

---

### 🔧 بناء التطبيق

**المتطلبات:**
- ☕ Java JDK 17 — [adoptium.net](https://adoptium.net)
- 🤖 Android SDK — [developer.android.com](https://developer.android.com/studio#command-line-tools-only)

**خطوات البناء:**
```cmd
cd PrayerTimesApp
gradlew.bat assembleDebug
```

**موقع الـ APK:**
```
app\build\outputs\apk\debug\app-debug.apk
```

**إعداد Android SDK بدون Android Studio:**
```cmd
setx ANDROID_HOME "C:\Android" /M
sdkmanager --licenses
sdkmanager "platforms;android-34" "build-tools;34.0.0"
```

---

### ⚙️ الإعدادات

- 🧮 **طريقة الحساب** — تُحدَّد تلقائياً حسب الموقع الجغرافي
- 📖 **المذهب الفقهي** — شافعي / مالكي / حنبلي أو حنفي
- ⏱️ **تعديل المواقيت** — دقائق + أو − لكل صلاة
- 🕐 **وقت الإقامة** — قابل للتعديل لكل صلاة
- 🕌 **وقت الجمعة** — يمكن تحديده يدوياً
- 🕰️ **صيغة الوقت** — 12 أو 24 ساعة
- 📢 **الأذان** — تفعيل / تعطيل لكل صلاة مستقلة

---

### 🔐 الأذونات المطلوبة

| الإذن | السبب |
|-------|-------|
| 📍 `ACCESS_FINE_LOCATION` | تحديد الموقع بـ GPS |
| ⏰ `SCHEDULE_EXACT_ALARM` | جدولة الأذان بدقة |
| 🔊 `FOREGROUND_SERVICE` | تشغيل الأذان في الخلفية |
| 🔔 `POST_NOTIFICATIONS` | إشعارات الأذان والإقامة |
| 🔄 `RECEIVE_BOOT_COMPLETED` | إعادة الجدولة بعد إعادة التشغيل |
| 🌐 `INTERNET` | المعايرة الأولى مع aladhan.com |

---

---

## 🇬🇧 English

### 🕌 What is Waqtuk?
**Waqtuk** (Arabic for "Your Time") is a free, open-source Android prayer times app.
It works **fully offline** after the first launch and supports **1500+ cities** worldwide.

---

### ✨ Features

| Feature | Description |
|---------|-------------|
| 🔭 **Accurate times** | Local astronomical calculation, no internet needed |
| 🌐 **Auto-calibration** | Connects to aladhan.com once to align with your official reference |
| 📍 **GPS auto-detect** | Locates you instantly without delay |
| 🏙️ **City search** | 1500+ cities, fully offline |
| 📢 **Real Adhan** | Full notification with instant stop button |
| 🕐 **Iqama times** | Fajr +17min, Maghrib +7min, others +10min |
| 🕌 **Jumuah card** | Appears automatically every Friday |
| 📅 **Hijri date** | Calculated and displayed automatically |
| 🌙 **Full Arabic UI** | RTL support, Arabic as default language |
| ⚙️ **Time adjustments** | +/− minutes per prayer manually |
| 🔄 **Boot persistence** | Reschedules alarms automatically after reboot |

---

### 🌍 Supported Calculation Methods

| Region | Method |
|--------|--------|
| 🇩🇿 Algeria, Morocco, Tunisia, Libya, Egypt | Egyptian General Authority |
| 🇸🇦 Saudi Arabia, Gulf, Yemen | Umm al-Qura (Makkah) |
| 🇵🇰 Pakistan, Afghanistan, India | University of Karachi |
| 🇮🇷 Iran | Tehran Geophysics Institute |
| 🇺🇸 North America | ISNA |
| 🌐 Europe & rest of world | Muslim World League (MWL) |

---

### 🕋 Prayer Times

| Prayer | Symbol |
|--------|--------|
| 🌅 Fajr | Before sunrise |
| ☀️ Sunrise | At dawn |
| 🌞 Dhuhr | Solar noon |
| ⛅ Asr | Afternoon |
| 🌇 Maghrib | At sunset |
| 🌙 Isha | After dusk |

---

### 🔧 Building the App

**Requirements:**
- ☕ Java JDK 17 — [adoptium.net](https://adoptium.net)
- 🤖 Android SDK — [developer.android.com](https://developer.android.com/studio#command-line-tools-only)

**Build steps:**
```cmd
cd PrayerTimesApp
gradlew.bat assembleDebug
```

**APK location:**
```
app\build\outputs\apk\debug\app-debug.apk
```

**Android SDK setup (without Android Studio):**
```cmd
setx ANDROID_HOME "C:\Android" /M
sdkmanager --licenses
sdkmanager "platforms;android-34" "build-tools;34.0.0"
```

---

### ⚙️ Settings

- 🧮 **Calculation method** — Auto-detected by geographic location
- 📖 **Juristic method** — Shafi/Maliki/Hanbali or Hanafi
- ⏱️ **Time adjustments** — +/− minutes per prayer
- 🕐 **Iqama offsets** — Adjustable per prayer
- 🕌 **Jumuah time** — Can be set manually
- 🕰️ **Time format** — 12h or 24h
- 📢 **Adhan** — Enable/disable per prayer independently

---

### 🔐 Permissions

| Permission | Reason |
|------------|--------|
| 📍 `ACCESS_FINE_LOCATION` | GPS location detection |
| ⏰ `SCHEDULE_EXACT_ALARM` | Precise adhan scheduling |
| 🔊 `FOREGROUND_SERVICE` | Play adhan in background |
| 🔔 `POST_NOTIFICATIONS` | Adhan & iqama notifications |
| 🔄 `RECEIVE_BOOT_COMPLETED` | Reschedule after reboot |
| 🌐 `INTERNET` | One-time calibration with aladhan.com |

---

### 📁 Project Structure

```
PrayerTimesApp/
├── 📂 audio/           AdhanService.java
├── 📂 calculation/     PrayerCalculator · MethodDetector
│                       TimezoneDetector · AladhanApiClient
├── 📂 location/        LocationHelper · CityDatabase (1500+ 🏙️)
├── 📂 model/           Prayer.java
├── 📂 receiver/        AlarmReceiver · BootReceiver
├── 📂 ui/              MainActivity · SettingsActivity
│                       SplashActivity · LocationSearchActivity
└── 📂 utils/           AlarmScheduler · AppPreferences · TimeUtils
```

---

<div align="center">

🕌 **بارك الله فيكم — May Allah bless you** 🕌

</div>

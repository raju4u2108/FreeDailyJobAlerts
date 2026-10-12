# FreeDailyJobAlert — Android WebView App

Android app for **FreeDailyJobAlert.in** — shows the live website in a native WebView with push notifications, offline screen, splash screen, and bottom navigation.

---

## Features
- ✅ Full WebView loading `https://freedailyjobalert.in`
- ✅ Real-time website content (always fresh from server)
- ✅ Splash screen with logo + fade animation
- ✅ Bottom nav bar: Home · Jobs · Results · Alerts
- ✅ Firebase push notifications (FCM)
- ✅ Offline fallback page
- ✅ Back button navigation (goes back in WebView history)
- ✅ External links open in browser (not in-app)
- ✅ Progress bar while page loads

---

## One-Time Setup Before Building

### Step 1 — Firebase
1. Go to: https://console.firebase.google.com/
2. Create project named **FreeDailyJobAlert**
3. Add Android app — package: `in.freedailyjobalert.app`
4. Download `google-services.json`
5. Copy it to: `app/google-services.json`
6. In Firebase → Cloud Messaging → note your **Server Key** (for sending notifications)

### Step 2 — App Icon
1. Replace `app/src/main/res/drawable/ic_logo.xml` with your PNG logo (192x192)
2. Open project in Android Studio
3. Right-click `res` → New → Image Asset → Launcher Icons → use your logo PNG
4. This auto-generates all mipmap sizes

### Step 3 — Keystore (for Play Store release)
```
keytool -genkey -v -keystore fdja-keystore.jks -alias fdja -keyalg RSA -keysize 2048 -validity 10000
```
Then fill in the keystore details in `app/build.gradle` under `signingConfigs.release`.

---

## Build Instructions

### Open in Android Studio
1. File → Open → select this folder
2. Wait for Gradle sync
3. Run on device or emulator

### Build Release APK
```
./gradlew assembleRelease
```
Output: `app/build/outputs/apk/release/app-release.apk`

### Build Release AAB (for Play Store)
```
./gradlew bundleRelease
```
Output: `app/build/outputs/bundle/release/app-release.aab`

---

## Sending Push Notifications

### Via Firebase Console (free, manual)
1. Firebase Console → Cloud Messaging → New Campaign
2. Title: e.g. "New UPSC Jobs 2026"
3. Message: e.g. "6000+ vacancies open — apply before Dec 31"
4. Target: All Users (topic: `all_users`)
5. Send

### Via API (automated from GovtBot)
You can trigger notifications from your server when GovtBot publishes a new post:

```bash
curl -X POST https://fcm.googleapis.com/fcm/send \
  -H "Authorization: key=YOUR_SERVER_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "to": "/topics/all_users",
    "notification": {
      "title": "New Govt Job Alert",
      "body": "New UPSC jobs posted — check now!"
    },
    "data": {
      "url": "https://freedailyjobalert.in/govt-jobs/"
    }
  }'
```

---

## Project Structure
```
FreeDailyJobAlert-Android/
├── app/
│   ├── src/main/
│   │   ├── java/in/freedailyjobalert/app/
│   │   │   ├── MainActivity.java           — WebView + offline + nav
│   │   │   ├── SplashActivity.java         — Splash with fade animation
│   │   │   └── MyFirebaseMessagingService.java — Push notifications
│   │   ├── res/
│   │   │   ├── layout/activity_main.xml    — Main layout
│   │   │   ├── layout/activity_splash.xml  — Splash layout
│   │   │   ├── values/strings.xml          — App strings
│   │   │   ├── values/colors.xml           — Brand colors
│   │   │   ├── values/styles.xml           — App themes
│   │   │   ├── drawable/ic_logo.xml        — Logo (replace with PNG)
│   │   │   ├── drawable/ic_notification.xml— Notification icon
│   │   │   └── drawable/btn_primary.xml    — Button style
│   │   ├── assets/offline.html             — No internet page
│   │   └── AndroidManifest.xml
│   ├── google-services.json                ← ADD THIS (from Firebase)
│   ├── build.gradle
│   └── proguard-rules.pro
├── build.gradle
├── settings.gradle
└── gradle/wrapper/gradle-wrapper.properties
```

---

## Package & Publish to Play Store
1. Build signed AAB: `./gradlew bundleRelease`
2. Go to: https://play.google.com/console/
3. Create app → FreeDailyJobAlert
4. Upload AAB
5. Fill store listing, screenshots, category: Tools or News & Magazines
6. Publish to Internal Testing first, then Production

---

Built for **FreeDailyJobAlert.in** — India's Daily Govt Job Alert Portal

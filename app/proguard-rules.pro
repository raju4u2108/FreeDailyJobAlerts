# FreeDailyJobAlert ProGuard Rules

# Keep WebView JavaScript interface
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Keep Firebase
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# Keep our app classes
-keep class in.freedailyjobalert.app.** { *; }

# Keep AndroidX
-keep class androidx.** { *; }

# General Android rules
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service

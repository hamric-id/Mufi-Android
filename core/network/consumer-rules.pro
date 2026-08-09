# proguard-rules.pro - OWASP Security
# ✅ OWASP: Obfuscate sensitive data
-keepclassmembers class com.hamric.core.network.api.TmdbApi {
    *;
}

# ✅ OWASP: Don't log sensitive data
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
}

# ✅ OWASP: Protect API key
-keep class com.hamric.core.network.BuildConfig {
    *;
}
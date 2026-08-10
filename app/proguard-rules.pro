# ============================================
# 1. General Android Rules
# ============================================

# Keep all classes that extend Android components
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View

# Keep custom view constructors
-keepclassmembers class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Keep Parcelable
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Keep R8 optimization
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keepattributes Signature

# ============================================
# 2. Kotlin Rules
# ============================================

# Keep Kotlin metadata
-keepclassmembers class kotlin.Metadata {
    public *;
}

# Keep Kotlin coroutines
-keepclassmembers class kotlinx.coroutines.** {
    *;
}
-keepclassmembers class kotlinx.coroutines.internal.MainDispatcherFactory {
    *;
}
-keepclassmembers class kotlinx.coroutines.CoroutineExceptionHandler {
    *;
}
-keepclassmembers class kotlin.coroutines.Continuation {
    *;
}

# Keep Kotlin reflection
-dontwarn kotlin.reflect.**

# Keep Kotlin companion objects
-keepclassmembers class ** {
    public static final ** Companion;
}

# ============================================
# 3. Hilt / Dagger Rules
# ============================================

# Keep Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class javax.annotation.** { *; }
-keep class com.google.errorprone.annotations.** { *; }

# Keep Hilt components
-keep class * extends dagger.hilt.android.components.ActivityComponent { *; }
-keep class * extends dagger.hilt.android.components.FragmentComponent { *; }
-keep class * extends dagger.hilt.android.components.ViewModelComponent { *; }
-keep class * extends dagger.hilt.android.components.ServiceComponent { *; }

# Keep Hilt view models
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}


# ============================================
# 4. Compose Rules
# ============================================

# Keep Compose
-keep class androidx.compose.** { *; }
-keepclassmembers class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Keep Compose preview
-keepclassmembers class ** {
    @androidx.compose.runtime.Composable *;
}

# Keep Compose annotations
-keep class androidx.compose.runtime.internal.StabilityInferred { *; }
-keep @interface androidx.compose.runtime.Stable

# ============================================
# 5. Retrofit / OkHttp Rules
# ============================================

# Keep Retrofit
-keep class retrofit2.** { *; }
-keepclassmembers class retrofit2.** { *; }
-dontwarn retrofit2.**

# Keep OkHttp
-keep class okhttp3.** { *; }
-keepclassmembers class okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**

# Keep Gson
-keep class com.google.gson.** { *; }
-dontwarn com.google.gson.**

# ============================================
# 6. Coil Rules
# ============================================

# Keep Coil
-keep class coil.** { *; }
-dontwarn coil.**

# ============================================
# 7. Model / Response Classes
# ============================================

# Keep model classes
-keep class com.hamric.core.model.** { *; }
-keep class com.hamric.core.network.response.** { *; }

# Keep classes used in API
-keep class com.hamric.core.network.api.** { *; }
-keepclassmembers class com.hamric.core.network.api.** {
    *;
}

# ============================================
# 8. Keep your app classes
# ============================================

# Keep ViewModels
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# Keep Hilt ViewModels
-keep public class ** extends androidx.lifecycle.ViewModel {
    public <init>(...);
}

# Keep Navigation
-keep class androidx.navigation.** { *; }

# ============================================
# 9. Remove debug logs (optional)
# ============================================

# Remove debug logs
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# ============================================
# 10. Keep your features
# ============================================

# Keep Genre feature
-keep class com.hamric.feature.genres.** { *; }

# Keep Movies feature
-keep class com.hamric.feature.movies.** { *; }

# Keep Details feature
-keep class com.hamric.feature.details.** { *; }

# ============================================
# 11. YouTube Player
# ============================================

# Keep YouTube Player
-keep class com.pierfrancescosoffritti.androidyoutubeplayer.** { *; }
-dontwarn com.pierfrancescosoffritti.androidyoutubeplayer.**

# ============================================
# 12. Testing (exclude from release)
# ============================================

# Remove test classes
-dontwarn org.mockito.**
-dontwarn org.junit.**
-dontwarn org.hamcrest.**
-dontwarn android.test.**

# ============================================
# 13. General optimizations
# ============================================

# Optimize
-optimizationpasses 5
-allowaccessmodification
-mergeinterfacesaggressively
-overloadaggressively
-repackageclasses ''
-flattenpackagehierarchy

# Keep exceptions
-keepclassmembers class * {
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Keep generic types
-keepattributes Signature

# Keep annotations
-keepattributes *Annotation*

# Keep inner classes
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep class names for reflection
-keepnames class * extends java.lang.annotation.Annotation
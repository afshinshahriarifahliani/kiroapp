# EmberFocus ProGuard / R8 rules

# Keep Kotlin metadata for reflection-light libraries.
-keepattributes *Annotation*, InnerClasses, Signature, Exceptions

# Koin
-keep class org.koin.** { *; }
-keepnames class * { @org.koin.core.annotation.* <methods>; }

# Room generated implementations are kept by the Room consumer rules automatically.

# Play Billing
-keep class com.android.billingclient.** { *; }

# Coroutines
-keepclassmembers class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**

# Keep ViewModels (instantiated reflectively by factories)
-keep class * extends androidx.lifecycle.ViewModel { <init>(...); }

# Keep data/domain models used by serialization-free flows (safety for R8 shrinking).
-keep class com.emberfocus.core.model.** { *; }

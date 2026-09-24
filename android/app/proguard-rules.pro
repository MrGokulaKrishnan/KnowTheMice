# Know The Mice Android Proguard rules
-keep class com.knowthemice.app.model.** { *; }
-keep class com.knowthemice.app.data.model.** { *; }
-keep class com.knowthemice.app.network.** { *; }
-keep class com.knowthemice.app.update.** { *; }
-keepclassmembers class com.knowthemice.app.update.** { *; }

# Gson ProGuard rules
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

-dontwarn java.lang.management.**
-dontwarn javax.management.**

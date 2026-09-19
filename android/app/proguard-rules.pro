# Know The Mice Android Proguard rules
-keep class com.knowthemice.app.model.** { *; }
-keep class com.knowthemice.app.data.model.** { *; }
-keep class com.knowthemice.app.network.** { *; }
-keepattributes *Annotation*
-dontwarn java.lang.management.**
-dontwarn javax.management.**

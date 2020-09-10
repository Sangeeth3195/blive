-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}

-keep class android.support.v7.widget.** { *; }

-keep public class com.google.android.gms.* { public *; }
-dontwarn com.google.android.gms.**
-keep class io.agora.**{*;}
-keep public class io.agora.* { public *; }
-dontwarn io.agora.**
-keep class agora.** { *; }
-keep class javax.annotation.** { *; }
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement


# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

# OkHttp platform used only on JVM and when Conscrypt dependency is available.
-dontwarn okhttp3.internal.platform.ConscryptPlatform

-keep public class org.slf4j.* { public *; }
-dontwarn org.slf4j.**

-keep public class com.twitter.sdk.* { public *; }
-dontwarn com.twitter.sdk.**

-keep class com.wang.avi.** { *; }
-keep class com.wang.avi.indicators.** { *; }

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

-keep public class com.bumptech.glide.integration.webp.WebpImage { *; }
-keep public class com.bumptech.glide.integration.webp.WebpFrame { *; }
-keep public class com.bumptech.glide.integration.webp.WebpBitmapFactory { *; }

# Proguard configuration for Jackson 2.x (fasterxml package instead of codehaus package)
-keep class com.fasterxml.jackson.databind.ObjectMapper {
    public <methods>;
    protected <methods>;
}
-keep class com.fasterxml.jackson.databind.ObjectWriter {
    public ** writeValueAsString(**);
}
-keepnames class com.fasterxml.jackson.** { *; }
-dontwarn com.fasterxml.jackson.databind.**

-keepnames class com.applandeo.materialcalendarview.** { *; }
-dontwarn com.applandeo.materialcalendarview.**

-keepnames class com.bumptech.glide.** { *; }
-dontwarn ccom.bumptech.glide.**

-keep class com.firebase.** { *; }
-keep class org.apache.** { *; }
-keepnames class com.fasterxml.jackson.** { *; }
-keepnames class javax.servlet.** { *; }
-keepnames class org.ietf.jgss.** { *; }
-dontwarn org.w3c.dom.**
-dontwarn org.joda.time.**
-dontwarn org.shaded.apache.**
-dontwarn org.ietf.jgss.**

-keep public class com.blive.** { *; }
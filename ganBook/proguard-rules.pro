-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-verbose
-dontpreverify
-allowaccessmodification
-mergeinterfacesaggressively

-keepattributes Signature, InnerClasses, EnclosingMethod

-keep @interface *
-keep class io.nivad.** { *; }
-keep class com.auth0.jwt.** { *; }
-keep class cn.pedant.SweetAlert.Rotate3dAnimation { *; }
-dontwarn org.**
-dontwarn javax.**
-dontwarn com.auth0.jwt.**
-keep class retrofit.** { *; }

-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

-keepclassmembers class * extends java.lang.Enum {
    <fields>;
    private <fields>;
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}


-keep enum org.greenrobot.eventbus.ThreadMode { *; }
-keep class com.amazonaws.** { *; }
-keep class com.ganbook.app.** { *; }
-keepclassmembers class com.ganbook.app.** { *; }

-keep class com.ganbook.models.** { *; }
-keepclassmembers class com.ganbook.models.** { *; }



-keep class com.ganbook.user.** { *; }
-keepclassmembers class com.ganbook.user.** { *; }

-keep class com.ganbook.activities.SplashActivity { *; }
-keepclassmembers class com.ganbook.activities.SplashActivity { *; }

-keep class com.ganbook.fragments.** { *; }
-keepclassmembers class com.ganbook.fragments.** { *; }

-keep class com.ganbook.communication.** { *; }
-keepclassmembers class * {
  public <init>(android.content.Context);
  }

# Stop warnings about missing unused classes
-keep class me.leolin.shortcutbadger.** { *; }

# keep everything in this package from being renamed only
-keepnames class me.leolin.shortcutbadger.** { *; }


-dontnote org.apache.http.params.HttpConnectionParams
-dontnote org.apache.http.params.CoreConnectionPNames
-dontnote org.apache.http.params.HttpParams
-dontnote org.apache.http.conn.scheme.LayeredSocketFactory
-dontnote org.apache.http.conn.scheme.SocketFactory
-dontnote org.apache.http.conn.scheme.HostNameResolver
-dontnote org.apache.http.conn.ConnectTimeoutException
-dontnote android.net.http.SslCertificate
-dontnote android.net.http.SslCertificate$DName
-dontnote android.net.http.SslError
-dontnote android.net.http.HttpResponseCache

-dontwarn kotlin.Unit
-dontwarn retrofit2.-KotlinExtensions
-dontwarn javax.annotation.**
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn com.google.common.annotations.**
-dontwarn com.amazonaws.util.json.**
-dontwarn okhttp3.**
-dontwarn org.bytedeco.javacpp.tools.**
-dontwarn retrofit2.**
-dontwarn org.bytedeco.javacpp.indexer.**
-dontwarn com.squareup.picasso.**
-dontwarn org.apache.http.params.**
-dontwarn android.net.http.**
-dontnote retrofit2.Platform
-dontwarn retrofit2.Platform$Java8





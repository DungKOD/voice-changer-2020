# [30] appodeal
# [29] cipher
# [28] fabric crashlytics
# [27] okhttp org.conscrypt
# [26] rxffmpeg
# [25] butterknife
# [24] mobileffmpeg
# [23] com.android
# [22] unity
# [21] mozilla
# [20] remove fqcn.of.js
# [19] retrofit
# [18] okhttp
# [17] realm
# [16] other
# [15] reactivex
# [14] other
# [13] other
# [12] other
# [11] kotlin retrofit rxandroid rxjava greendao
# [10] javax optimizations other reformat
# [9] unity
# [8] exo player

# ANDROID
-android
-dontpreverify
-repackageclasses ''
# Zelix Not Support
-allowaccessmodification
-optimizations !code/simplification/arithmetic
-optimizations !class/unboxing/enum

-keep class android.** { *; }
-dontwarn android.**
-keep class androidx.** { *; }
-dontwarn androidx.**
-keep class com.android.** { *; }
-dontwarn com.android.**
-keep class android.support.** { *; }
-dontwarn android.support.**
-keep interface android.support.** { *; }
-keep class com.android.** { *; }
-dontwarn com.android.**

-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable

-keep public class * extends java.lang.annotation.Annotation
-keep public class * extends java.lang.Exception

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# JAVA
-keep class java.** { *; }
-dontwarn java.**

-keep class javax.** { *; }
-dontwarn javax.**

# Kotlin
-keepclassmembers class **$WhenMappings {
    <fields>;
}
-keep class kotlin.Metadata { *; }
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepnames class kotlinx.coroutines.android.AndroidExceptionPreHandler {}
-keepnames class kotlinx.coroutines.android.AndroidDispatcherFactory {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}
-dontwarn kotlinx.atomicfu.**

# ENUM
-keepclassmembers class * extends java.lang.Enum {
    <fields>;
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# IAB
-keep class com.android.vending.billing.** { *; }
-keep class com.android.billingclient.** { *; }

# GOOGLE
-keep class com.google.** { *; }
-dontwarn com.google.**

# FACEBOOK
-keep class com.facebook.** { *; }
-dontwarn com.facebook.**
-keep class bolts.** { *; }
-dontwarn bolts.**

# TWITTER
-keep class com.twitter.** { *; }
-dontwarn com.twitter.**

# UNITY
-keep class bitter.jnibridge.* { *; }
-keep class com.unity3d.player.* { *; }
-keep class org.fmod.* { *; }
-ignorewarnings

-keep class bitter.jnibridge.** { *; }
-keep class com.unity3d.** { *; }
-keep class com.unity3d.player.** { *; }
-keep class org.fmod.** { *; }
-keep class srs.** { *; }
-keep class ro.** { *; }

# APACHE
-keep class org.apache.** { *; }
-dontwarn org.apache.**

# OKHTTP
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**
-dontwarn okio.**
-dontwarn javax.annotation.Nullable
-dontwarn javax.annotation.ParametersAreNonnullByDefault

# OKHTTP 3
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**
-keep class okio.** { *; }
-dontwarn okio.**
-keep class org.conscrypt.** { *; }
-dontwarn org.conscrypt.**

-dontwarn javax.annotation.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
-dontwarn org.codehaus.mojo.animal_sniffer.*
-dontwarn okhttp3.internal.platform.ConscryptPlatform

# RETROFIT
-keep class retrofit2.** { *; }
-dontwarn retrofit2.**
-dontnote retrofit2.Platform
-dontwarn retrofit2.Platform$Java8
-keepattributes Signature
-keepattributes Exceptions
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

-dontwarn retrofit.**
-keep class retrofit.** { *; }
-dontwarn sun.misc.Unsafe
-dontwarn com.octo.android.robospice.retrofit.RetrofitJackson**
-dontwarn retrofit.appengine.UrlFetchClient
-keepattributes Signature
-keepattributes Exceptions
-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}
-keep class com.google.gson.** { *; }
-keep class com.google.inject.** { *; }
-keep class org.apache.http.** { *; }
-keep class org.apache.james.mime4j.** { *; }
-keep class javax.inject.** { *; }
-keep class retrofit.** { *; }
-dontwarn org.apache.http.**
-dontwarn android.net.http.AndroidHttpClient
-dontwarn retrofit.**

-dontwarn sun.misc.**

# RxJava RxAndroid
-keep class rx.schedulers.Schedulers {
    public static <methods>;
}
-keep class rx.schedulers.ImmediateScheduler {
    public <methods>;
}
-keep class rx.schedulers.TestScheduler {
    public <methods>;
}
-keep class rx.schedulers.Schedulers {
    public static ** test();
}
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    long producerNode;
    long consumerNode;
}
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}
-dontwarn sun.misc.Unsafe

-keep class io.reactivex.** { *; }
-dontwarn io.reactivex.**

# REALM
-keepnames public class * extends io.realm.RealmObject
-keep class io.realm.annotations.RealmModule
-keep @io.realm.annotations.RealmModule class *
-keep class io.realm.internal.Keep
-keep @io.realm.internal.Keep class *
-keep class io.realm.** { *; }
-dontwarn javax.**
-dontwarn io.realm.**

# greenDAO 3
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties
-dontwarn org.greenrobot.greendao.database.**
-dontwarn rx.**
### greenDAO 2
-keepclassmembers class * extends de.greenrobot.dao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties

# SQL CIPHER
-keep class net.sqlcipher.** { *; }
-dontwarn net.sqlcipher.**

# GLIDE
-keep class com.bumptech.** { *; }
-dontwarn com.bumptech.**
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

-keep class jp.wasabeef.** { *; }
-dontwarn jp.wasabeef.**

# EVENTBUS
-keepattributes *Annotation*
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

# SENTRY
-keepattributes LineNumberTable,SourceFile
-dontwarn org.slf4j.**
-dontwarn javax.**
-keep class io.sentry.event.Event { *; }
-keep class org.slf4j.** { *; }
-dontwarn org.slf4j.**

# FABRIC. CRASHLYTICS
-keep class io.fabric.** { *; }
-dontwarn io.fabric.**

-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**

# JSOUP
-keep class org.jsoup.** { *; }
-dontwarn org.jsoup.**
-keeppackagenames org.jsoup.nodes

# GSON
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-keep,allowobfuscation @interface com.google.gson.annotations.**

# JSON
-keep class org.json.** { *; }
-dontwarn org.json.**

# XML
-keep class org.xmlpull.** { *; }
-dontwarn org.xmlpull.**

# SPRING
-keep class org.androidannotations.** { *; }
-dontwarn org.androidannotations.**
-keep class org.springframework.** { *; }
-dontwarn org.springframework.**

# BUTTER KNIFE
-keep @interface butterknife.*
-keepclasseswithmembers class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembers class * {
    @butterknife.* <methods>;
}
-keepclasseswithmembers class * {
    @butterknife.On* <methods>;
}
-keep class **$$ViewInjector {
    public static void inject(...);
    public static void reset(...);
}
-keep class **$$ViewBinder {
    public static void bind(...);
    public static void unbind(...);
}
-if   class **$$ViewBinder
-keep class <1>
-keep class **_ViewBinding {
    <init>(<1>, android.view.View);
}
-if   class **_ViewBinding
-keep class <1>

-keep public class * implements butterknife.Unbinder { public <init>(**, android.view.View); }

-keep class butterknife.*
-keepclasseswithmembernames class * { @butterknife.* <methods>; }
-keepclasseswithmembernames class * { @butterknife.* <fields>; }

# HAWK
-keep class com.orhanobut.** { *; }
-dontwarn com.orhanobut.**

# JUNIT
-keep class org.junit.** { *; }
-dontwarn org.junit.**

# EXO
-keepclassmembernames class com.google.android.exoplayer2.ui.PlayerControlView {
  java.lang.Runnable hideAction;
  void hideAfterTimeout();
}

# SENTRY
-keepattributes LineNumberTable,SourceFile
-dontwarn org.slf4j.**
-dontwarn javax.**
-keep class io.sentry.event.Event { *; }

# MOZILLA
-keep class org.mozilla.** { *; }
-dontwarn org.mozilla.**

# MOBILE FFMPEG
-keep class com.arthenica.mobileffmpeg.Config {
    native <methods>;
    void log(int, byte[]);
    void statistics(int, float, float, long , int, double, double);
}
-keep class com.arthenica.mobileffmpeg.AbiDetect {
    native <methods>;
}

# RxFFmpeg
-dontwarn io.microshow.rxffmpeg.**
-keep class io.microshow.rxffmpeg.**{*;}

# OTHERS
-keep class com.akexorcist.** { *; }
-dontwarn com.akexorcist.**

-keep class com.liuguangqiang.** { *; }
-dontwarn com.liuguangqiang.**

-keep class autovalue.** { *; }
-dontwarn autovalue.**

-keep class org.joda.** { *; }
-dontwarn org.joda.**

-keep class org.mozilla.** { *; }
-dontwarn org.mozilla.**

-keep class java.awt.** { *; }
-dontwarn java.awt.**

-keep class com.e1.** { *; }
-dontwarn com.e1.**

-keep class cz.intik.** { *; }
-dontwarn cz.intik.**

-keep class com.github.** { *; }
-dontwarn com.github.**

-keep class com.gu.** { *; }
-dontwarn com.gu

-keep class com.bumptech.** { *; }
-dontwarn com.bumptech.**

-keep class org.wysaid.** { *; }
-dontwarn org.wysaid.**

-keep class com.otaliastudios.** { *; }
-dontwarn com.otaliastudios.**

-keep class ezvcard.** { *; }
-dontwarn ezvcard.**

-keep class freemarker.** { *; }
-dontwarn freemarker.**

-keep class com.mancj.** { *; }
-dontwarn com.mancj.**

-keep class net.sourceforge.** { *; }
-dontwarn net.sourceforge.**

-keep class info.lamatricexiste.** { *; }
-dontwarn info.lamatricexiste.**

-keep class net.glxn.** { *; }
-dontwarn net.glxn.**

-keep class org.xbill.** { *; }
-dontwarn org.xbill.**

# APPODEAL DEPENDS
-keep class com.appodeal.** { *; }
-dontwarn com.appodeal.**

-keep class com.adcolony.** { *; }
-dontwarn com.adcolony.**

-keep class com.amazon.** { *; }
-dontwarn com.amazon.**

-keep class com.applovin.** { *; }
-dontwarn com.applovin.**

-keep class com.chartboost.** { *; }
-dontwarn com.chartboost.**

-keep class com.inmobi.** { *; }
-dontwarn com.inmobi.**

-keep class com.integralads.** { *; }
-dontwarn com.integralads.**

-keep class com.ironsource.** { *; }
-dontwarn com.ironsource.**

-keep class com.mintegral.** { *; }
-dontwarn com.mintegral.**

-keep class com.my.target.** { *; }
-dontwarn com.my.target.**

-keep class com.ogury.** { *; }
-dontwarn com.ogury.**

-keep class com.smaato.** { *; }
-dontwarn com.smaato.**

-keep class com.startapp.** { *; }
-dontwarn com.startapp.**

-keep class com.tapjoy.** { *; }
-dontwarn com.tapjoy.**

-keep class com.vungle.** { *; }
-dontwarn com.vungle.**

-keep class com.yandex.** { *; }
-dontwarn com.yandex.**

# REMOVE LOG
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

# APP
-keep class com.audioeffect.voicechanger.entities.** { *; }
-dontwarn com.audioeffect.voicechanger.entities.**
-keep class com.audioeffect.voicechanger.bridge.** { *; }
-dontwarn com.audioeffect.voicechanger.bridge.**
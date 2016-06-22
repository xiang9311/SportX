# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Java\android-sdk-windows/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-dontoptimize
-dontpreverify
-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }
#==================gson==========================
-dontwarn com.google.**
-keep class com.google.gson.** {*;}
#==================protobuf======================
-dontwarn com.google.**
-keep class com.google.protobuf.** {*;}
#==================baidu map=====================
-keep class com.baidu.** {*;}
-keep class vi.com.** {*;}
-dontwarn com.baidu.**
#==================rongyun=======================
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
 public *;
}

-keepattributes Exceptions,InnerClasses

-keep class io.rong.** {*;}

-keep class * implements io.rong.imlib.model.MessageContent{*;}

-keepattributes Signature

-keepattributes *Annotation*

-keep class sun.misc.Unsafe { *; }

-keep class com.google.gson.examples.android.model.** { *; }

-keepclassmembers class * extends com.sea_monster.dao.AbstractDao {
 public static java.lang.String TABLENAME;
}
-keep class **$Properties
-dontwarn org.eclipse.jdt.annotation.**

-keep class com.ultrapower.** {*;}
#======================== imageloader =========================
-dontwarn com.nostra13.universalimageloader.**

-keep class com.nostra13.universalimageloader.** { *; }
#======================== sharesdk ============================
-keep class cn.sharesdk.**{*;}
-keep class com.sina.**{*;}
-keep class **.R$* {*;}
-keep class **.R{*;}
-dontwarn cn.sharesdk.**
-dontwarn **.R$*
-keep class m.framework.**{*;}
-keep class android.net.http.SslError
-keep class com.mob.tools.utils
-keep class com.xxx.share.onekey.theme.classic.EditPage

-keep class com.xiang.view**
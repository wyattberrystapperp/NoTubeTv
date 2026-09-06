-keepattributes SourceFile,LineNumberTable,*Annotation*,Signature,InnerClasses,EnclosingMethod,JavascriptInterface

# 1. Critical: JavaScript Interfaces
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
-keep class com.ycngmn.notubetv.utils.NetworkBridge { *; }
-keep class com.ycngmn.notubetv.utils.ExitBridge { *; }

# 2. OkHttp & Coroutines
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.coroutines.** { volatile <fields>; }
-dontwarn kotlinx.coroutines.**
-dontwarn org.slf4j.impl.StaticLoggerBinder

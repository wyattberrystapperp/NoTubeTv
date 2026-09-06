# --- NoTubeTV R8 Production Safety Net ---
-keepattributes SourceFile,LineNumberTable,*Annotation*,Signature,InnerClasses,EnclosingMethod,JavascriptInterface

# 1. Critical: JavaScript Interfaces (SponsorBlock & ExitBridge)
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
-keep class com.ycngmn.notubetv.utils.NetworkBridge { *; }
-keep class com.ycngmn.notubetv.utils.ExitBridge { *; }

# 2. Multiplatform WebView & WebKit
-keep class com.multiplatform.webview.** { *; }
-dontwarn com.multiplatform.webview.**

# 3. Ktor & OkHttp Reflection
-keep class io.ktor.** { *; }
-dontwarn io.ktor.**
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**

# 4. Kotlin Coroutines & ViewModel
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.coroutines.** { volatile <fields>; }
-dontwarn kotlinx.coroutines.**
-keep class com.ycngmn.notubetv.ui.YoutubeVM { *; }
-dontwarn org.slf4j.impl.StaticLoggerBinder

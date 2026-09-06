package com.ycngmn.notubetv.utils
import android.app.Activity
import android.webkit.JavascriptInterface

class ExitBridge(private val activity: Activity) {
    @JavascriptInterface
    fun onExitCalled() { activity.finish() }
}

package com.ycngmn.notubetv

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Bundle
import android.view.*
import android.webkit.*
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.ycngmn.notubetv.utils.*

class MainActivity : ComponentActivity() {
    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar
    private lateinit var backScript: String

    private val audioPermLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.setFormat(PixelFormat.OPAQUE)
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)

        backScript = readRaw(this, R.raw.back_bridge)
        val userScripts = fetchScripts(this)

        val root = FrameLayout(this).apply {
            setBackgroundColor(Color.BLACK)
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }

        webView = WebView(this).apply {
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            overScrollMode = View.OVER_SCROLL_NEVER
            isVerticalScrollBarEnabled = false
            isHorizontalScrollBarEnabled = false

            settings.apply {
                javaScriptEnabled = true
                userAgentString = "Mozilla/5.0 Cobalt/20 (Sony, PS4, Wireless)"
                domStorageEnabled = true
                useWideViewPort = true
                mediaPlaybackRequiresUserGesture = false
                setSupportZoom(false)
                builtInZoomControls = false
                cacheMode = WebSettings.LOAD_DEFAULT
                setGeolocationEnabled(false)
                allowFileAccess = false
                allowContentAccess = false
            }

            val cm = CookieManager.getInstance()
            cm.setAcceptCookie(true)
            cm.setAcceptThirdPartyCookies(this, true)

            addJavascriptInterface(ExitBridge(this@MainActivity), "ExitBridge")
            addJavascriptInterface(NetworkBridge(this), "NetworkBridge")

            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    evaluateJavascript(userScripts, null)
                    progressBar.visibility = View.GONE
                }
            }

            webChromeClient = object : WebChromeClient() {
                override fun onPermissionRequest(request: PermissionRequest) {
                    if (PermissionRequest.RESOURCE_AUDIO_CAPTURE in request.resources) {
                        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                            audioPermLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                        request.grant(request.resources)
                    }
                }
            }
        }

        progressBar = ProgressBar(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
            )
            indeterminateDrawable?.setTint(Color.RED)
        }

        root.addView(webView)
        root.addView(progressBar)
        setContentView(root)
        webView.loadUrl("https://www.youtube.com/tv")
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) { event?.startTracking(); return true }
        if (keyCode == KeyEvent.KEYCODE_MENU || keyCode == KeyEvent.KEYCODE_SETTINGS) {
            webView.evaluateJavascript("window.modernUI && window.modernUI();", null)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyLongPress(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            webView.evaluateJavascript("window.modernUI && window.modernUI();", null)
            return true
        }
        return super.onKeyLongPress(keyCode, event)
    }

    override fun onBackPressed() {
        if (progressBar.visibility == View.VISIBLE) super.onBackPressed()
        else webView.evaluateJavascript(backScript, null)
    }

    override fun onDestroy() {
        webView.destroy()
        super.onDestroy()
    }
}

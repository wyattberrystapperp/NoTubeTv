package com.ycngmn.notubetv.ui.screens

import android.app.Activity
import android.view.View
import android.view.WindowManager
import android.webkit.CookieManager
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.multiplatform.webview.web.LoadingState
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewState
import com.ycngmn.notubetv.R
import com.ycngmn.notubetv.ui.YoutubeVM
import com.ycngmn.notubetv.utils.ExitBridge
import com.ycngmn.notubetv.utils.NetworkBridge
import com.ycngmn.notubetv.utils.fetchScripts
import com.ycngmn.notubetv.utils.permHandler
import com.ycngmn.notubetv.utils.readRaw

@Composable
fun YoutubeWV(youtubeVM: YoutubeVM = viewModel()) {

    val context = LocalContext.current
    val activity = context as Activity

    val state = rememberWebViewState("https://www.youtube.com/tv")
    val navigator = rememberWebViewNavigator()
    val backScript = remember { readRaw(context, R.raw.back_bridge) }

    val jsScript = youtubeVM.scriptData

    val loadingState = state.loadingState
    val exitTrigger = remember { mutableStateOf(false) }
    val menuTrigger = youtubeVM.menuTrigger
    LaunchedEffect(menuTrigger) {
        if (menuTrigger > 0L) {
            navigator.evaluateJavaScript("window.modernUI && window.modernUI();")
        }
    }

    // Translate native back-presses to 'escape' button press
    BackHandler {
        if (state.loadingState is LoadingState.Finished)
            navigator.evaluateJavaScript(backScript)
        else exitTrigger.value = true
    }

    // Load bundled local userscript immediately (standalone, 0ms latency)
    LaunchedEffect(Unit) {
        youtubeVM.setScript(fetchScripts(context))
    }

    LaunchedEffect(loadingState, jsScript) {
        if (loadingState is LoadingState.Finished && jsScript != null) {
            navigator.evaluateJavaScript(jsScript)
        }
    }
    // If any update found, show the dialog.
    // Update dialog disabled (standalone)
    // If exit button is pressed, 'finish the activity' aka 'exit the app'.
    if (exitTrigger.value) activity.finish()

    // This is the loading screen
    val loading = state.loadingState as? LoadingState.Loading
    if (loading != null) SplashLoading(loading.progress)

    WebView(
        modifier = Modifier.fillMaxSize(),
        state = state,
        navigator = navigator,
        platformWebViewParams = permHandler(context),
        captureBackPresses = false,
        onCreated = { webView ->

            // Window layout handled in MainActivity

            // Set up cookies
            val cookieManager = CookieManager.getInstance()
            cookieManager.setAcceptCookie(true)
            cookieManager.setAcceptThirdPartyCookies(webView, true)
            cookieManager.flush()

            state.webSettings.apply {
                // This user agent provides native like experience.
                // "PS4" for 4K. "Wired" for previews.
                customUserAgentString = "Mozilla/5.0 Cobalt/20 (Sony, PS4, Wireless)"
                isJavaScriptEnabled = true

                androidWebSettings.apply {
                    //isDebugInspectorInfoEnabled = true
                    useWideViewPort = true
                    domStorageEnabled = true
                    databaseEnabled = true
                    hideDefaultVideoPoster = true
                    mediaPlaybackRequiresUserGesture = false
                }
            }

            webView.apply {

                // Bridges the exit button click on the website to handle it natively.
                addJavascriptInterface(ExitBridge(exitTrigger), "ExitBridge")

                /*
                Youtube's content security policy doesn't allow calling fetch on
                3rd party websites (eg. SponsorBlock api). This bridge counters that
                handling the requests on the native side. */
                addJavascriptInterface(NetworkBridge(navigator), "NetworkBridge")

                // Enables hardware acceleration
                setLayerType(View.LAYER_TYPE_NONE, null)
                overScrollMode = View.OVER_SCROLL_NEVER
                settings.setSupportZoom(false)
                settings.builtInZoomControls = false
                // Set the zoom to 25% to fit the screen. Side-effect of viewport spoofing.
                // Native 100% scale for 1080p UI

                // Hide scrollbars
                isVerticalScrollBarEnabled = false
                isHorizontalScrollBarEnabled = false
            }
        }
    )
}
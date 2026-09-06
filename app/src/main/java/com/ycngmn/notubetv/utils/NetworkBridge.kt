package com.ycngmn.notubetv.utils
import android.webkit.JavascriptInterface
import android.webkit.WebView
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject

class NetworkBridge(private val webView: WebView) {
    private val client = OkHttpClient()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @JavascriptInterface
    fun fetch(url: String, videoId: String) {
        scope.launch {
            try {
                val req = Request.Builder().url(url).build()
                val body = client.newCall(req).execute().use { it.body?.string() ?: "" }
                val filtered = if (body.startsWith("[")) filterSponsorBlock(body, videoId) else body
                val js = "window.onNetworkBridgeResponse(${JSONObject.quote(filtered)});"
                withContext(Dispatchers.Main) { webView.evaluateJavascript(js, null) }
            } catch (_: Exception) {}
        }
    }

    private fun filterSponsorBlock(body: String, videoId: String): String {
        val json = JSONArray(body)
        for (i in 0 until json.length()) {
            val item = json.getJSONObject(i)
            if (item.getString("videoID") == videoId) return item.toString()
        }
        return ""
    }
}

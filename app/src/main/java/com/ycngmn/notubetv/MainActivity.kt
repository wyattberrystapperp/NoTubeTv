package com.ycngmn.notubetv

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.ycngmn.notubetv.ui.YoutubeVM
import com.ycngmn.notubetv.ui.screens.YoutubeWV
import com.ycngmn.notubetv.ui.theme.NoTubeTVTheme

class MainActivity : ComponentActivity() {
    private val youtubeVM: YoutubeVM by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Standard 1080p window layout
        setContent { NoTubeTVTheme { Box(Modifier.fillMaxSize()) { YoutubeWV(youtubeVM) } } }
    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) event?.startTracking()
        if (keyCode == KeyEvent.KEYCODE_MENU || keyCode == KeyEvent.KEYCODE_SETTINGS) {
            youtubeVM.triggerMenu(); return true
        }
        return super.onKeyDown(keyCode, event)
    }
    override fun onKeyLongPress(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) { youtubeVM.triggerMenu(); return true }
        return super.onKeyLongPress(keyCode, event)
    }
}

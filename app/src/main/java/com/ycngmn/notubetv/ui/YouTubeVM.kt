package com.ycngmn.notubetv.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.ycngmn.notubetv.utils.ReleaseData

class YoutubeVM : ViewModel() {
    private val _scriptData = mutableStateOf<String?>(null)
    val scriptData: String? get() = _scriptData.value
    private val _updateData = mutableStateOf<ReleaseData?>(null)
    val updateData: ReleaseData? get() = _updateData.value
    private val _menuTrigger = mutableStateOf(0L)
    val menuTrigger: Long get() = _menuTrigger.value

    fun setScript(data: String) { _scriptData.value = data }
    fun setUpdate(data: ReleaseData) { _updateData.value = data }
    fun triggerMenu() { _menuTrigger.value = System.currentTimeMillis() }
}

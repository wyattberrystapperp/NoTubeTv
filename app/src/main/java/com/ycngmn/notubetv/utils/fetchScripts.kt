package com.ycngmn.notubetv.utils

import android.content.Context

fun fetchScripts(context: Context): String {
    return context.assets.open("userscripts.js")
        .bufferedReader()
        .use { it.readText() }
}

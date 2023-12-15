package com.cyy.transapp.util

import android.content.Context

object FileUtil {
    fun readRawToTxt(context: Context, resourceId: Int): String {
        val inputStream = context.resources.openRawResource(resourceId)
        return inputStream.bufferedReader().use { it.readText() }
    }
}
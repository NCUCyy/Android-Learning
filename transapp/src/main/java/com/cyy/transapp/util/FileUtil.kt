package com.cyy.transapp.util

import android.content.Context
import java.io.File

object FileUtil {
    fun readRawToTxt(context: Context, resourceId: Int): String {
        val inputStream = context.resources.openRawResource(resourceId)
        return inputStream.bufferedReader().use { it.readText() }
    }

    fun readFileAsString(filePath: String): String {
        return File(filePath).readText(Charsets.UTF_8)
    }
}
package com.cyy.transapp.util

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.ByteArrayOutputStream
import java.io.File

object FileUtil {
    fun readRawToTxt(context: Context, resourceId: Int): String {
        val inputStream = context.resources.openRawResource(resourceId)
        return inputStream.bufferedReader().use { it.readText() }
    }

    fun readFileAsString(filePath: String): String {
        return File(filePath).readText(Charsets.UTF_8)
    }

    /**
     * 选择图库中的照片得到Uri，先转化为Bitmap，再转为String存储到Room
     */
    fun uriToString(context: Activity, uri: Uri): String {
        val stream = ByteArrayOutputStream()
        // 得到bitmap
        val bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri));
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        // 转为byte数组
        val bytes = stream.toByteArray();
        // 得到string
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    /**
     * 读取Room中的String，转为Bitmap进行显示
     */
    fun stringToImageBitmap(string: String): ImageBitmap {
        val bytes = Base64.decode(string, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size).asImageBitmap()
    }

}
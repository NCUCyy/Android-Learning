package com.cyy.app.ch04

import android.util.Base64
import androidx.lifecycle.ViewModel


class CyperViewModel : ViewModel() {
    fun encodeBase64(content: String): String {
        val bytes = content.toByteArray()
        return String(Base64.encode(bytes, Base64.DEFAULT))
    }

    fun decodeBase64(cyperContent: String): String {
        return String(Base64.decode(cyperContent, Base64.DEFAULT))
    }
}
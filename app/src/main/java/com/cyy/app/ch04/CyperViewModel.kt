package com.cyy.app.ch04

import android.util.Base64
import androidx.compose.runtime.MutableState
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class CyperViewModel : ViewModel() {
    val output:MutableLiveData<String> = MutableLiveData<String>()
    fun encodeBase64(content: String) {
        val bytes = content.toByteArray()
        output.value = String(Base64.encode(bytes, Base64.DEFAULT))
    }

    fun decodeBase64(cyperContent: String) {
        output.value = String(Base64.decode(cyperContent, Base64.DEFAULT))
    }
}
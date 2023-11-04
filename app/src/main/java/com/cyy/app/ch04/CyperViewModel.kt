package com.cyy.app.ch04

import android.util.Base64
import androidx.compose.runtime.MutableState
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class CyperViewModel : ViewModel() {
    private val _input: MutableStateFlow<String> = MutableStateFlow("")
    val input = _input.asStateFlow()

    // 私有量只能内部修改数（可变的状态流）
    private val _output: MutableStateFlow<String> = MutableStateFlow("")

    // 获取StateFlow，只读（界面只需要获取它的值，不需要去修改它，所有的修改应该交给CyperViewModel来完成）
    val output = _output.asStateFlow()

    /**
     * 用于修改页面的输入
     */
    fun changeInput(input: String) {
        _input.value = input
    }

    /**
     * 用于修改页面的输出
     */
    fun encodeBase64(content: String) {
        val bytes = content.toByteArray()
        _output.value = String(Base64.encode(bytes, Base64.DEFAULT))
    }

    /**
     * 用于修改页面的输出
     */
    fun decodeBase64(cyperContent: String) {
        _output.value = String(Base64.decode(cyperContent, Base64.DEFAULT))
    }
}
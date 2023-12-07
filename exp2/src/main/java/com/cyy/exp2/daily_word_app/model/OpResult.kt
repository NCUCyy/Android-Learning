package com.cyy.exp2.daily_word_app.model

sealed class OpResult<out R> {
    // 未开始请求
    object NotBegin : OpResult<Nothing>()

    // 成功时，返回图片的URL
    data class Success<out T>(val data: T) : OpResult<T>()

    // 错误时，返回报错信息errorDesc
    data class Error<out T>(val errorDesc: T) : OpResult<T>()

    // 请求中
    object Loading : OpResult<Nothing>()
}
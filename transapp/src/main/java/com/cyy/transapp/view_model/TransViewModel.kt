package com.cyy.transapp.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cyy.transapp.model.OpResult
import com.cyy.transapp.pojo.StarWord
import com.cyy.transapp.repository.StarWordRepository
import com.cyy.transapp.repository.TransRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.concurrent.thread

/**
 * 用于TransActivity
 * 1. 用于联网翻译
 */
class TransViewModel(
    private val transRepository: TransRepository,
    private val userId: Int,
    private val query: String,
    private val starRepository: StarWordRepository
) : ViewModel() {

    private val _transState = MutableStateFlow<OpResult<Any>>(OpResult.NotBegin)
    val transState = _transState.asStateFlow()


    private val _isStared = MutableStateFlow(false)
    val isStared = _isStared.asStateFlow()

    init {
        // 一创建就翻译
        translate(query)
        checkStared()
    }

    // 收藏
    fun starWord() = viewModelScope.launch {
        starRepository.insert(StarWord(userId, query))
        // 更新状态
        checkStared()
    }

    // 取消收藏
    fun unstarWord() = viewModelScope.launch {
        // TODO：小技巧（普通的delete函数，需要先查询出来，再根据每个字段一一比对，找到一样的删除这个记录；更方便的，我们可以通过@Query注解的当时，自动以删除方式——即根据userId和word来删除）
        starRepository.deleteByUserIdAndWord(userId, query)
        // 更新状态
        checkStared()
    }

    private fun checkStared() = viewModelScope.launch {
        Log.i("TransViewModel", "checkStared: ${_isStared.value}")
        val starWord = starRepository.getStarWordByUserIdAndWord(userId, query)
        _isStared.value = starWord != null
        Log.i("TransViewModel", "checkStared: ${_isStared.value}")
    }

    // 内部外部都会调用这个（外部：网络不好，初始化时的请求失败了，就需要在外面重新请求，即调用translate()）
    fun translate(query: String) {
        thread {
            _transState.value = OpResult.Loading
            transRepository.translate(query) { it: OpResult<Any> ->
                _transState.value = it
            }
        }
    }
}

class TransViewModelFactory(
    private val transRepository: TransRepository,
    private val userId: Int,
    private val query: String,
    private val starRepository: StarWordRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransViewModel(transRepository, userId, query, starRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

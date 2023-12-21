package com.cyy.transapp.view_model.trans

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cyy.transapp.model.OpResult
import com.cyy.transapp.pojo.TransRecord
import com.cyy.transapp.repository.SentenceRepository
import com.cyy.transapp.repository.TransRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.concurrent.thread

/**
 * 用于查询模块的ViewModel
 * 1. 保存输入的query
 * 2. 显示查询历史
 * 3、清空查询历史
 */
class QueryViewModel(
    val userId: Int,
    private val transRepository: TransRepository,
    private val sentenceRepository: SentenceRepository
) : ViewModel() {
    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()
    private val _sentenceState = MutableStateFlow<OpResult<Any>>(OpResult.NotBegin)
    val sentenceState = _sentenceState.asStateFlow()

    init {
        requestSentence()
    }

    // 所有的翻译记录
    var transRecords: StateFlow<List<TransRecord>> = transRepository.getAllTransRecords().stateIn(
        initialValue = emptyList(),
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000)
    )

    fun updateQuery(input: String) {
        _query.value = input
    }

    fun clearAllTransRecords() = viewModelScope.launch {
        transRepository.deleteAll()
    }

    fun requestSentence() {
        thread {
            _sentenceState.value = OpResult.Loading
            sentenceRepository.requestSentence {
                _sentenceState.value = it
            }
        }
    }

    fun clearQuery() {
        // 清空输入框
        _query.value = ""
    }
}

class QueryViewModelFactory(
    val userId: Int,
    private val transRepository: TransRepository,
    private val sentenceRepository: SentenceRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QueryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QueryViewModel(userId, transRepository, sentenceRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
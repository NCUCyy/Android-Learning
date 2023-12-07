package com.cyy.exp2.daily_word_app.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cyy.exp2.daily_word_app.model.OpResult
import com.cyy.exp2.daily_word_app.repository.SentenceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SentenceViewModel(private val sentenceRepository: SentenceRepository) : ViewModel() {
    private val _sentenceState = MutableStateFlow<OpResult<Any>>(OpResult.NotBegin)
    val sentenceState = _sentenceState.asStateFlow()

    init {
        loadSentence()
    }

    fun loadSentence() {
        // TODO：在ViewModel中修改页面的状态值
        _sentenceState.value = OpResult.Loading
        sentenceRepository.requestSentence { it: OpResult<Any> ->
            _sentenceState.value = it
        }
    }
}


class SentenceViewModelFactory(private val sentenceRepository: SentenceRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SentenceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SentenceViewModel(sentenceRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
package com.cyy.transapp.view_model

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cyy.app.word_bank.model.Word
import com.cyy.transapp.model.OpResult
import com.cyy.transapp.model.Vocabulary
import com.cyy.transapp.repository.VocabularyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.concurrent.thread

class WordViewModel(
    private val vocabularyRepository: VocabularyRepository,
    private val vocabulary: String,
    private val context: Activity
) : ViewModel() {
    private val _wordList = MutableStateFlow(Word())
    val wordList: StateFlow<Word> = _wordList.asStateFlow()

    private val _loadVocabularyState = MutableStateFlow<OpResult<Any>>(OpResult.NotBegin)
    val loadVocabularyState: StateFlow<OpResult<Any>> = _loadVocabularyState.asStateFlow()

    init {
        // 创建的时候就加载整个字典
        loadVocabulary()
    }

    private fun loadVocabulary() {
        _loadVocabularyState.value = OpResult.Loading
        viewModelScope.launch {
            thread {
                _wordList.value =
                    vocabularyRepository.getVocabularyWord(
                        context,
                        Vocabulary.valueOf(vocabulary)
                    )
                _loadVocabularyState.value = OpResult.Success("加载完成")
            }
        }
    }
}

class WordViewModelFactory(
    private val vocabularyRepository: VocabularyRepository,
    private val vocabulary: String,
    private val context: Activity
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WordViewModel(vocabularyRepository, vocabulary, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
package com.cyy.transapp.view_model

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cyy.transapp.model.Vocabulary
import com.cyy.transapp.repository.VocabularyRepository

class WordViewModel(
    private val vocabularyRepository: VocabularyRepository,
    private val vocabulary: String,
    private val context: Activity
) : ViewModel() {
    val word =
        vocabularyRepository.getVocabularyWord(context, Vocabulary.valueOf(vocabulary))
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
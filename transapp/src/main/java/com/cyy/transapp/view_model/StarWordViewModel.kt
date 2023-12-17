package com.cyy.transapp.view_model

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cyy.transapp.pojo.StarWord
import com.cyy.transapp.repository.StarWordRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StarWordViewModel(
    private val userId: Int,
    private val starWordRepository: StarWordRepository
) : ViewModel() {
    val starWords = starWordRepository.getAllByUserId(userId).stateIn(
        initialValue = listOf(),
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000)
    )

    var wordExistState = mutableStateOf(false)

    fun insert(starWord: StarWord) = viewModelScope.launch {
        starWordRepository.insert(starWord)
    }

    fun delete(starWord: StarWord) = viewModelScope.launch {
        starWordRepository.delete(starWord)
    }
}

class StarWordViewModelFactory(
    private val userId: Int,
    private val starWordRepository: StarWordRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StarWordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StarWordViewModel(userId, starWordRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
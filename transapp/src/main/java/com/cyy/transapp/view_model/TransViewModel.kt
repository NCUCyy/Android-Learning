package com.cyy.transapp.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cyy.transapp.model.OpResult
import com.cyy.transapp.repository.TransRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.concurrent.thread

/**
 * 用于TransActivity
 * 1. 用于联网翻译
 */
class TransViewModel(private val transRepository: TransRepository) : ViewModel() {

    private val _transState = MutableStateFlow<OpResult<Any>>(OpResult.NotBegin)
    val transState = _transState.asStateFlow()

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
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransViewModel(transRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

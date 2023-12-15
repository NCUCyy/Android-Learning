package com.cyy.transapp.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cyy.transapp.repository.ListenRepository

class ListenViewModel(private val listenRepository: ListenRepository) : ViewModel() {
    fun getALlListenResource() = listenRepository.listenResources
}

class ListenViewModelFactory(
    private val listenRepository: ListenRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ListenViewModel(listenRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
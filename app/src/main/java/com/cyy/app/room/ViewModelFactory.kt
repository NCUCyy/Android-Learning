package com.cyy.app.room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class GenericViewModelFactory<T : ViewModel>(
    private val repository: Any,
    private val viewModelClass: Class<T>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(viewModelClass)) {
            @Suppress("UNCHECKED_CAST")
            return viewModelClass.getDeclaredConstructor(repository.javaClass)
                .newInstance(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
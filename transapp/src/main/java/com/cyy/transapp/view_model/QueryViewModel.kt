package com.cyy.transapp.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cyy.transapp.repository.QueryRepository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class QueryViewModel(private val queryRepository: QueryRepository) : ViewModel() {
    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()
    fun updateQuery(input: String) {
        _query.value = input
    }

}

class QueryViewModelFactory(
    private val queryRepository: QueryRepository,
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QueryViewModel(queryRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
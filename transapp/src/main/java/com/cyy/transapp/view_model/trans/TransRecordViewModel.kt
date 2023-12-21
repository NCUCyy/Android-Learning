package com.cyy.transapp.view_model.trans

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cyy.transapp.pojo.TransRecord
import com.cyy.transapp.repository.TransRepository
import kotlinx.coroutines.launch

/**
 * 用于TransActivity
 * 1. 更新查词历史
 */
class TransRecordViewModel(private val transRecordRepository: TransRepository) : ViewModel() {
    fun updateHistory(cur: TransRecord) = viewModelScope.launch {
        val last = transRecordRepository.getByWord(cur.word)
        if (last == null) {
            transRecordRepository.insert(cur)
        } else{
            last.freq++
            last.lastQueryTime = cur.lastQueryTime
            transRecordRepository.update(last)
        }
    }
}

class TransRecordViewModelFactory(private val repository: TransRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransRecordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransRecordViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
package com.cyy.transapp.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CurUserViewModel() : ViewModel() {

}

class CurUserViewModelFactory() :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CurUserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CurUserViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

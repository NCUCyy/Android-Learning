package com.cyy.exp2.psychological_test.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class UserScreenViewModel :ViewModel(){

}


class UserScreenViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserScreenViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
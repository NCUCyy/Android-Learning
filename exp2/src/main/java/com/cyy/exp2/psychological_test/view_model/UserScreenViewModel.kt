package com.cyy.exp2.psychological_test.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cyy.exp2.psychological_test.pojo.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserScreenViewModel(loginUser: User) : ViewModel() {
    private val _username = MutableStateFlow(loginUser.username)
    val username = _username.asStateFlow()
    private val _password = MutableStateFlow(loginUser.password)
    val password = _password.asStateFlow()
    fun updateUsername(input: String) {
        _username.value = input
    }

    fun updatePassword(input: String) {
        _password.value = input
    }
}


class UserScreenViewModelFactory(private val loginUser: User) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserScreenViewModel(loginUser) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
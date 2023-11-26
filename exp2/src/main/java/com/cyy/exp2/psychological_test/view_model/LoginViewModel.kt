package com.cyy.exp2.psychological_test.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginViewModel : ViewModel() {
    private val _username = MutableStateFlow("")
    val username = _username.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _registerUsername = MutableStateFlow("")
    val registerUsername = _registerUsername.asStateFlow()

    private val _registerPassword = MutableStateFlow("")
    val registerPassword = _registerPassword.asStateFlow()

    private val _registerSex = MutableStateFlow("男")
    val registerSex = _registerSex.asStateFlow()

    private val _isRegister = MutableStateFlow(false)
    val isRegister = _isRegister.asStateFlow()

    fun afterRegister() {
        // 完成注册后，将注册信息复制到登录信息
        _username.value = _registerUsername.value
        _password.value = _registerPassword.value
        _registerUsername.value = ""
        _registerPassword.value = ""
        _registerSex.value = "男"
    }

    fun afterLogin() {
        // 完成登录后，将登录信息清空
        _username.value = ""
        _password.value = ""
        _registerUsername.value = ""
        _registerPassword.value = ""
        _registerSex.value = "男"
    }

    fun updateIsRegister(isRegister: Boolean) {
        _isRegister.value = isRegister
    }

    fun updateUsername(username: String) {
        _username.value = username
    }

    fun updatePassword(password: String) {
        _password.value = password
    }

    fun updateRegisterUsername(username: String) {
        _registerUsername.value = username
    }

    fun updateRegisterPassword(password: String) {
        _registerPassword.value = password
    }

    fun updateRegisterSex(sex: String) {
        _registerSex.value = sex
    }
}

class LoginViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

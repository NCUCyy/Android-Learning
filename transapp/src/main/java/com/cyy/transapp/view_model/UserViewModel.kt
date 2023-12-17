package com.cyy.transapp.view_model

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cyy.transapp.model.ConfirmPasswordState
import com.cyy.transapp.model.LoginState
import com.cyy.transapp.model.RegisterState
import com.cyy.transapp.model.UsernameAndPasswordState
import com.cyy.transapp.model.UsernameState
import com.cyy.transapp.pojo.User
import com.cyy.transapp.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {
    // 用户名(register/loginToMainActivity)
    private val _username = MutableStateFlow("")
    val username = _username.asStateFlow()

    // 密码(register/loginToMainActivity)
    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    // 确认密码(register)
    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword = _confirmPassword.asStateFlow()


    // 用户名状态(register)
    val usernameState = mutableStateOf(UsernameState.NOT_BEGIN)

    // 确认密码状态(register)
    val confirmPasswordState = mutableStateOf(ConfirmPasswordState.NOT_BEGIN)


    // 用户名和密码状态(loginToMainActivity)
    val usernameAndPasswordState = mutableStateOf(UsernameAndPasswordState.NOT_BEGIN)

    // 注册状态(register)---observe
    val registerState = MutableLiveData(RegisterState.NOT_BEGIN)

    // 登录状态(loginToMainActivity)---observe
    val loginState = MutableLiveData(LoginState.NOT_BEGIN)

    lateinit var loginUser: User
    private fun updateLoginUser(user: User) {
        loginUser = user
    }

    fun login() = viewModelScope.launch {
        val user = userRepository.getByUsernameAndPassword(_username.value, _password.value)
        if (user != null) {
            usernameAndPasswordState.value = UsernameAndPasswordState.CORRECT
            // TODO：注意给loginUser赋值 和 LoginState.SUCCESS 的顺序
            updateLoginUser(user)
            loginState.value = LoginState.SUCCESS
            // TODO：Initial--Today
        } else {
            usernameAndPasswordState.value = UsernameAndPasswordState.ERROR
            loginState.value = LoginState.FAILED
        }
    }

    fun register() = viewModelScope.launch {
        if (usernameState.value == UsernameState.AVAILABLE && confirmPasswordState.value == ConfirmPasswordState.AVAILABLE) {
            var user = User(_username.value, _password.value)
            userRepository.insert(user)
            user = userRepository.getByUsernameAndPassword(_username.value, _password.value)
            // TODO：注意给loginUser赋值 和 RegisterState.SUCCESS 的顺序
            updateLoginUser(user)
            registerState.value = RegisterState.SUCCESS
            // TODO：Initial--Today
        } else {
            registerState.value = RegisterState.FAILED
        }
    }

    private fun judgeExist(username: String) = viewModelScope.launch {
        val user = userRepository.getByUsername(username)
        if (user != null) {
            usernameState.value = UsernameState.EXIST
        } else {
            usernameState.value = UsernameState.AVAILABLE
        }
    }

    fun updateUsername(username: String) {
        _username.value = username
        if (username == "") {
            usernameState.value = UsernameState.EMPTY
        } else {
            // 同步修改
            judgeExist(username)
        }
    }

    fun updatePassword(password: String) {
        _password.value = password
    }

    fun updateConfirmPassword(confirmPassword: String) {
        _confirmPassword.value = confirmPassword
        if (_password.value != confirmPassword) {
            confirmPasswordState.value = ConfirmPasswordState.DIFFERENT
        } else {
            confirmPasswordState.value = ConfirmPasswordState.AVAILABLE
        }
    }

    fun update(vararg user: User) = viewModelScope.launch {
        userRepository.update(*user)
    }

    fun delete(vararg user: User) = viewModelScope.launch {
        userRepository.delete(*user)
    }

    fun deleteAll() = viewModelScope.launch {
        userRepository.deleteAll()
    }
}

class UserViewModelFactory(private val userRepository: UserRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

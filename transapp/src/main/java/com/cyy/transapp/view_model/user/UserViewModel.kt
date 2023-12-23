package com.cyy.transapp.view_model.user

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cyy.transapp.model.ConfirmPasswordState
import com.cyy.transapp.model.LoginState
import com.cyy.transapp.model.RegisterState
import com.cyy.transapp.model.UsernameAndPasswordState
import com.cyy.transapp.model.UsernameState
import com.cyy.transapp.pojo.Plan
import com.cyy.transapp.pojo.User
import com.cyy.transapp.repository.PlanRepository
import com.cyy.transapp.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.concurrent.thread

class UserViewModel(
    private val userRepository: UserRepository,
    private val planRepository: PlanRepository
) : ViewModel() {
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
    // 老办法：直接用MutableState
    //  val usernameState = mutableStateOf(UsernameState.NOT_BEGIN)
    // 正确方法？用MutableStateFlow
    private val _usernameState = MutableStateFlow(UsernameState.NOT_BEGIN)
    val usernameState = _usernameState.asStateFlow()

    // 确认密码状态(register)
    private val _confirmPasswordState = MutableStateFlow(ConfirmPasswordState.NOT_BEGIN)
    val confirmPasswordState = _confirmPasswordState.asStateFlow()


    // 用户名和密码状态(loginToMainActivity)
    private val _usernameAndPasswordState = MutableStateFlow(UsernameAndPasswordState.NOT_BEGIN)
    val usernameAndPasswordState = _usernameAndPasswordState.asStateFlow()

    // 注册状态(register)---observe
    val registerState = MutableLiveData(RegisterState.NOT_BEGIN)

    // 登录状态(loginToMainActivity)---observe
    val loginState = MutableLiveData(LoginState.NOT_BEGIN)

    // TODO：当前登录的用户（login/register成功后再赋值），只是为了获得userId，才有这个变量！（不想用username！）
    lateinit var loginUser: User

    private fun initLoginUser(user: User) {
        loginUser = user
    }

    fun login() = viewModelScope.launch {
        val user = userRepository.getByUsernameAndPassword(_username.value, _password.value)
        if (user != null) {
            _usernameAndPasswordState.value = UsernameAndPasswordState.CORRECT
            // TODO：注意给loginUser赋值 和 LoginState.SUCCESS 的顺序
            initLoginUser(user)
            loginState.value = LoginState.SUCCESS
            thread {
                // 过5秒再清空数据！
                Thread.sleep(5000)
                clearAll()
            }
        } else {
            _usernameAndPasswordState.value = UsernameAndPasswordState.ERROR
            loginState.value = LoginState.FAILED
        }
    }

    private fun clearAll() {
        _username.value = ""
        _password.value = ""
        _confirmPassword.value = ""
        _usernameState.value = UsernameState.NOT_BEGIN
        _confirmPasswordState.value = ConfirmPasswordState.NOT_BEGIN
        _usernameAndPasswordState.value = UsernameAndPasswordState.NOT_BEGIN
    }

    fun register() = viewModelScope.launch {
        if (usernameState.value == UsernameState.AVAILABLE && confirmPasswordState.value == ConfirmPasswordState.AVAILABLE) {
            var user = User(_username.value, _password.value)
            userRepository.insert(user)
            user = userRepository.getByUsernameAndPassword(_username.value, _password.value)
            // TODO：注意给loginUser赋值 和 RegisterState.SUCCESS 的顺序
            initLoginUser(user)
            registerState.value = RegisterState.SUCCESS
            // TODO：Initial--Plan---默认给个未选择（用于取消学习计划时使用！）
            planRepository.insert(Plan(user.id, "未选择"))
        } else {
            registerState.value = RegisterState.FAILED
        }
    }

    private fun judgeExist(username: String) = viewModelScope.launch {
        val user = userRepository.getByUsername(username)
        if (user != null) {
            _usernameState.value = UsernameState.EXIST
        } else {
            _usernameState.value = UsernameState.AVAILABLE
        }
    }

    fun updateUsername(username: String) {
        _username.value = username
        if (username == "") {
            _usernameState.value = UsernameState.EMPTY
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
            _confirmPasswordState.value = ConfirmPasswordState.DIFFERENT
        } else {
            _confirmPasswordState.value = ConfirmPasswordState.AVAILABLE
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

class UserViewModelFactory(
    private val userRepository: UserRepository,
    private val planRepository: PlanRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(userRepository, planRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

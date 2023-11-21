package com.cyy.app.room

import android.util.Log
import androidx.compose.animation.EnterTransition.Companion.None
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.gson.internal.NonNullElementWrapperList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 *ViewModel 的作用是向界面提供数据，它以一种可以感知生命周期的方式保存应用的界面数据，不受配置变化的影响。
 * 它会将应用的界面数据与 Activity 和 Fragment 类区分开，让您更好地遵循OO设计原则中的单一职责：
 * （一）activity 和 fragment 负责将数据绘制到屏幕上
 * （二）ViewModel 则负责保存并处理界面所需的所有数据。
 *
 * ViewModel 中获取数据的具体方式是通过持有的 Repository 对象来进行操作的。
 * 因此从这个角度来看，ViewModel 实际上充当了数据仓库和界面之间的通信中心。
 */
// ViewModel 会在配置更改（如旋转设备）后继续存在。
class UserViewModel(private val repository: UserRepository) : ViewModel() {
    private var _mode: MutableStateFlow<String> = MutableStateFlow("login")
    val mode = _mode.asStateFlow()
    private var _loginUser: MutableStateFlow<User?> = MutableStateFlow(null)
    val loginUser = _loginUser.asStateFlow()

    private var _loginRes: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val loginRes = _loginRes.asStateFlow()
    private var _registerRes: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val registerRes = _registerRes.asStateFlow()

    fun setLoginUser(user: User) {
        _loginRes.value = true
        _loginUser.value = user
    }

    fun login(username: String, password: String) = viewModelScope.launch {
        _mode.value = "login"
        var user = repository.getByUsername(username)
        Log.i(
            "UserViewModel",
            "checkUsernameAndPwd: ${user}"
        )
        if (user != null && user.password == password) {
            setLoginUser(user)
        } else {
            _loginRes.value = false
        }
    }

    fun register(vararg user: User) = viewModelScope.launch {
        _mode.value = "register"
        val selectedUser = repository.getByUsername(user[0].username)
        if (selectedUser != null) {
            _registerRes.value = false
        } else {
            repository.insert(*user)
            _registerRes.value = true
            val user = repository.getByUsername(user[0].username)
            setLoginUser(user)
        }
    }

    fun update(vararg user: User) = viewModelScope.launch {
        repository.update(*user)
    }

    fun delete(vararg user: User) = viewModelScope.launch {
        repository.delete(*user)
    }

    fun deleteAll() = viewModelScope.launch {
        repository.deleteAll()
    }

}

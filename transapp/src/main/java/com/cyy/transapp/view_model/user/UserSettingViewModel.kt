package com.cyy.transapp.view_model.user

import android.app.Activity
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cyy.transapp.model.UsernameState
import com.cyy.transapp.pojo.User
import com.cyy.transapp.repository.UserRepository
import com.cyy.transapp.util.FileUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UserSettingViewModel(
    private val userId: Int,
    private val context: Activity,
    private val userRepository: UserRepository,
) :
    ViewModel() {
    // 当前登录的用户
    var curUser: StateFlow<User> = userRepository.getFlowById(userId).stateIn(
        initialValue = User(),
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(0)
    )

    init {
        viewModelScope.launch {
            val user = userRepository.getById(userId)
            // 属性的初始化
            _avatar.value = user.avatar
            _username.value = user.username
            _password.value = user.password
            _profile.value = user.profile
        }
    }

    private val _avatar = MutableStateFlow("")
    val avatar = _avatar.asStateFlow()

    private val _username = MutableStateFlow("")
    val username = _username.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()
    private val _profile = MutableStateFlow("")
    val profile = _profile.asStateFlow()

    private val _usernameState = MutableStateFlow(UsernameState.NOT_BEGIN)
    val usernameState = _usernameState.asStateFlow()

    private val _isEdit = MutableStateFlow(false)
    val isEdit = _isEdit.asStateFlow()
    fun beginEdit() {
        this._isEdit.value = true
    }

    fun saveEdit() {
        if (_usernameState.value == UsernameState.AVAILABLE || _usernameState.value == UsernameState.NOT_BEGIN) {
            viewModelScope.launch {
                val user = userRepository.getById(userId)
                user.avatar = _avatar.value
                user.username = _username.value
                user.password = _password.value
                user.profile = _profile.value
                userRepository.update(user)
            }
            _usernameState.value = UsernameState.NOT_BEGIN
            _isEdit.value = false
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
        when (username) {
            "" -> {
                _usernameState.value = UsernameState.EMPTY
            }

            curUser.value.username -> {
                _usernameState.value = UsernameState.AVAILABLE
            }

            else -> {
                // 同步修改
                judgeExist(username)
            }
        }
    }

    fun updatePassword(password: String) {
        _password.value = password
    }

    fun updateProfile(profile: String) {
        _profile.value = profile
    }

    fun updateAvatar(imageUri: Uri) {
        _avatar.value = FileUtil.uriToString(context, imageUri)
        saveAvatar()
    }

    private fun saveAvatar() = viewModelScope.launch {
        val user = userRepository.getById(userId)
        user.avatar = _avatar.value
        userRepository.update(curUser.value)
    }
}

class UserSettingViewModelFactory(
    private val userId: Int,
    private val context: Activity,
    private val userRepository: UserRepository,
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserSettingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserSettingViewModel(userId, context, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
package com.cyy.transapp.view_model.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cyy.transapp.pojo.User
import com.cyy.transapp.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UserSettingViewModel(
    private val userId: Int,
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
            _iconId.value = user.iconId
            _username.value = user.username
            _password.value = user.password
            _profile.value = user.profile
        }
    }

    private val _iconId = MutableStateFlow(0)
    val iconId = _iconId.asStateFlow()

    private val _username = MutableStateFlow("")
    val username = _username.asStateFlow()
    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()
    private val _profile = MutableStateFlow("")
    val profile = _profile.asStateFlow()

    fun updateUsername(username: String) {
        _username.value = username
    }

    fun updatePassword(password: String) {
        _password.value = password
    }

    fun updateProfile(profile: String) {
        _profile.value = profile
    }
}

class UserSettingViewModelFactory(
    private val userId: Int,
    private val userRepository: UserRepository,
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserSettingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserSettingViewModel(userId, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
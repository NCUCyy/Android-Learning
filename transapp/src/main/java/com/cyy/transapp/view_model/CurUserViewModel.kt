package com.cyy.transapp.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cyy.transapp.pojo.User
import com.cyy.transapp.repository.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/**
 * 保存当前登录的用户---curUser（根据UserId查询得到，ViewModel初始化的时候得到）
 */
class CurUserViewModel(
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
}

class CurUserViewModelFactory(
    private val userId: Int,
    private val userRepository: UserRepository,
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CurUserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CurUserViewModel(userId, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

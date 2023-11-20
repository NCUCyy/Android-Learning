package com.cyy.app.room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
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

    val allUser: StateFlow<List<User>> = repository.allUser.stateIn(
        initialValue = emptyList(),
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000)
    )

    fun insert(vararg user: User) = viewModelScope.launch {
        repository.insert(*user)
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

// 为了创建 ViewModel 对象，需要使用 ViewModelProvider.Factory。
class UserViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
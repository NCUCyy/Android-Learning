package com.cyy.app.room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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
package com.cyy.app.room

import android.util.Log
import androidx.compose.animation.EnterTransition.Companion.None
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
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
import kotlinx.coroutines.flow.toList
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
class RecordViewModel(private val repository: RecordRepository, private val loginUserId: Int) :
    ViewModel() {
    var records: StateFlow<List<Record>> = repository.getByUserId(loginUserId).stateIn(
        initialValue = emptyList(),
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000)
    )

    fun insert(vararg record: Record) = viewModelScope.launch {
        repository.insert(*record)
    }

    fun update(vararg record: Record) = viewModelScope.launch {
        repository.update(*record)
    }

    fun delete(vararg record: Record) = viewModelScope.launch {
        repository.delete(*record)
    }

    fun deleteAll() = viewModelScope.launch {
        repository.deleteAll()
    }

}

class RecordViewModelRepository(private val repository: RecordRepository, private val userId: Int) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecordViewModel(repository, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

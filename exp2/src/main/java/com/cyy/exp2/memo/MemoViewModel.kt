package com.cyy.exp2.memo

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

data class Memo(
    val id: Int,
    val content: String,
    val modifyTime: LocalDateTime,
    val createTime: LocalDateTime
)

class MemoViewModel : ViewModel() {
    // 数据本身
    private val _memos: MutableStateFlow<SnapshotStateList<Memo>> = MutableStateFlow(
        mutableStateListOf()
    )

    // 用于界面显示
    val memos = _memos.asStateFlow()

    // memo的id
    private var cnt: Int = 0

    fun add(content: String) {
        val memo = Memo(
            cnt++,
            content = content,
            createTime = LocalDateTime.now(),
            modifyTime = LocalDateTime.now()
        )
        _memos.value.add(memo)
    }
}
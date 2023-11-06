package com.cyy.exp2.memo

import android.util.Log
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

data class Memo(
    val id: Int,
    var content: String,
    var modifyTime: LocalDateTime,
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

    private val _cur: MutableStateFlow<Memo?> = MutableStateFlow(null)
    val cur = _cur.asStateFlow()

    private val _input: MutableStateFlow<String> = MutableStateFlow("")
    val input = _input.asStateFlow()


    init {
        _memos.value.add(Memo(1, "test", LocalDateTime.now(), LocalDateTime.now()))
        _memos.value.add(Memo(2, "test", LocalDateTime.now(), LocalDateTime.now()))
        _memos.value.add(Memo(3, "test", LocalDateTime.now(), LocalDateTime.now()))
    }


    fun setCur(memo: Memo) {
        _cur.value = memo
    }

    fun initInput() {
        _input.value = _cur.value?.content!!
    }

    fun changeInput(input: String) {
        Log.i("--------change-----------", input)
        _input.value = input
        _cur.value!!.content = input
    }

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
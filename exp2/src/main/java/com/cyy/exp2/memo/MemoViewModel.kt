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
        _memos.value.add(Memo(1, "今日：学习Android基础", LocalDateTime.now(), LocalDateTime.now()))
        _memos.value.add(Memo(4, "操作系统实验报告！", LocalDateTime.now(), LocalDateTime.now()))
        _memos.value.add(Memo(5, "Android实验报告！！！", LocalDateTime.now(), LocalDateTime.now()))
        _memos.value.add(Memo(6, "看论文！！！！！！", LocalDateTime.now(), LocalDateTime.now()))
        _memos.value.add(Memo(7, "光声图像理论基础学习", LocalDateTime.now(), LocalDateTime.now()))
        _memos.value.add(Memo(8, "代码复现", LocalDateTime.now(), LocalDateTime.now()))
    }

    fun setCur(memo: Memo) {
        _cur.value = memo
        _input.value = _cur.value!!.content
    }

    fun changeInput(input: String) {
        Log.i("--------change-----------", input)
        _input.value = input
        updateCur(input)
    }

    private fun updateCur(content: String) {
        _cur.value!!.content = content
        _cur.value!!.modifyTime = LocalDateTime.now()
    }

    fun add() {
        val memo = Memo(
            cnt++,
            content = "",
            createTime = LocalDateTime.now(),
            modifyTime = LocalDateTime.now()
        )
        _memos.value.add(memo)
        setCur(memo)
    }
}
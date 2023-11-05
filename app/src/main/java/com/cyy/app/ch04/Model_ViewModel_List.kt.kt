package com.cyy.app.ch04

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ListVM : ViewModel() {
    // 注意必须用SnapShotStateList<>！！！
    // 本质的数据
    private val _lst: MutableStateFlow<SnapshotStateList<String>> = MutableStateFlow(
        mutableStateListOf()
    )

    // 用于界面显示
    val lst = _lst.asStateFlow()

    /**
     * 初始化函数
     */
    init {
        _lst.value.add("123123")
        _lst.value.add("123123")
        _lst.value.add("123123")
    }

    /**
     * 修改数据
     */
    fun add(content: String) {
        _lst.value.add(content)
    }
}

class MainActivity1 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Screen()
        }
    }
}

@Preview
@Composable
fun Screen(vm: ListVM = viewModel()) {
    // 获取数据
    val lst = vm.lst.collectAsState()
    Column {
        Column {
            lst.value.forEach {
                Card {
                    Text(text = it)
                }
            }
        }
        Button(onClick = {
            // 修改数据
            vm.add("insert")
        }) {
            Text("点击添加")
        }

    }
}
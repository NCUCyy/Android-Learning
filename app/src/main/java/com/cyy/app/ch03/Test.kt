package com.cyy.app.ch03

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.tooling.preview.Preview


@SuppressLint("UnrememberedMutableState")
@Composable
@Preview
fun ListTest() {
    val list =
        mutableStateListOf<String>()
    Log.i("-----------", "123123")
    list.add("init")
    Box {
        Column {
            LazyColumn {
                items(list) {
                    Row {
                        Text(text = it)
                    }
                }
            }
            Button(onClick = {
                list.add("add")
            }) {
                Text(text = "点击添加内容")
            }
        }
    }
}
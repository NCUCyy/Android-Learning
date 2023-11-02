package com.cyy.app.ch03

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.tooling.preview.Preview

@SuppressLint("UnrememberedMutableState")
@Preview
@Composable
fun test() {
    val lst: MutableState<SnapshotStateList<String>> = rememberSaveable(stateSaver = CustomSaver) {
        mutableStateOf(mutableStateListOf<String>("123"))
    }
    Column {
        LazyColumn() {
            items(lst.value) {
                Row {
                    Text(text = it)
                }
            }
        }
        Button(onClick = { lst.value.add("666") }) {
            Text(text = "点击添加")
        }
    }
}

object CustomSaver : Saver<SnapshotStateList<String>, Bundle> {
    override fun restore(value: Bundle): SnapshotStateList<String>? {
        val lst: SnapshotStateList<String> = mutableStateListOf()
        val size = value.getInt("listSize")
        for (i in 0 until size) {
            lst.add(value.getString("$i")!!)
        }
        return lst
    }

    override fun SaverScope.save(value: SnapshotStateList<String>): Bundle? {
        return Bundle().apply {
            for (i in 0 until value.size) {
                putString("$i", value[i])
            }
            putInt("listSize", value.size)
        }
    }
}
package com.cyy.app.ch03

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import kotlinx.parcelize.Parcelize


// 需要实现Parcelize接口
@Parcelize
data class User(val name: String, val gender: String) : Parcelable

/**
 * 方式一：
 * - rememberSaveable{ mutableState<User>() }
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun Method_1() {
    // 旋转后数据不会丢失
    val userState = rememberSaveable {
        mutableStateOf(User("cyy", "male"))
    }
    val input = remember { mutableStateOf("") }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Text(text = userState.value.toString(), fontSize = 12.sp)
            TextField(
                value = input.value,
                onValueChange = { it: String -> input.value = it }
            )
            TextButton(
                onClick = {
                    val (name: String, gender: String) = input.value.split(" ")
                    userState.value = User(name, gender)
                    // 清空输入框
                    input.value = ""
                }) {
                Text(text = "点击更新")
                Icon(imageVector = Icons.Filled.Check, contentDescription = null)
            }
        }
    }
}


/**
 * 方式二：
 * - 实现Saver接口：
 * - 恢复：restore()
 * - 保存：save()
 */
object UserSaver : Saver<User, Bundle> {
    // 恢复
    override fun restore(value: Bundle): User? {
        return value.getString("name")?.let { name: String ->
            value.getString("gender")?.let { gender: String ->
                User(
                    name,
                    gender
                )
            }
        }
    }

    // 保存
    override fun SaverScope.save(value: User): Bundle? {
        // 测试
        Log.i("--------User", value.toString())
        return Bundle().apply {
            putString("name", value.name)
            putString("gender", value.gender)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun Method_2() {
    val userState = rememberSaveable(stateSaver = UserSaver) {
        mutableStateOf(User("cyy", "female"))
    }
    val input = remember { mutableStateOf("") }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Text(text = userState.value.toString(), fontSize = 12.sp)
            TextField(
                value = input.value,
                onValueChange = { it: String -> input.value = it }
            )
            TextButton(
                onClick = {
                    val (name: String, gender: String) = input.value.split(" ")
                    userState.value = User(name, gender)
                    // 清空输入框
                    input.value = ""
                }) {
                Text(text = "点击更新")
                Icon(imageVector = Icons.Filled.Check, contentDescription = null)
            }
        }
    }
}

/**
 * 方式三：
 * - listSaver(User, Any)
 */
@Composable
@Preview
fun Method_3() {
    val userSaver2 = listSaver<User, Any>(
        save = {
            // it解析为User
            listOf(it.name, it.gender)
        },
        restore = {
            // it解析为List<Any>
            User(it[0] as String, it[1] as String)
        }
    )
    val userState = rememberSaveable(stateSaver = userSaver2) {
        mutableStateOf(User("cyy", "male"))
    }
}
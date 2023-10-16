package com.cyy.exp1.diceGame

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.ArrayList
import java.util.Objects

class DiceHistoryActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var history = intent.getSerializableExtra("history", ArrayList::class.java)
        setContent() {
            HistoryScreen(history)
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(history: ArrayList<*>?) {
    val context = LocalContext.current as Activity
    var query by remember { mutableStateOf("") }
    var queryHistory = remember { mutableListOf<String>() }
    // 初始化显示
    if (query == "") {
        queryHistory.clear()
        queryHistory.addAll(history!![history.size - 1] as MutableList<String>)
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Yellow)
    ) {
        Button(
            onClick = {
                // 返回GameWin/LoseActivity
                // 为了实现：点击按钮，结束当前意图(返回代码为：RESULT_OK)
                val intent = Intent()
                intent.putExtra("message", "返回")
                // 传递一个意图参数参数
                context.setResult(Activity.RESULT_OK, intent)
                // 结束当前意图(回到过来的地方)
                context.finish()
            }, modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "返回")
            Text("返回", fontSize = 10.sp, textAlign = TextAlign.Center)
        }

        Column {
            Row(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    value = query,
                    onValueChange = {
                        query = it
                        if (query != "") {
                            val tmp = query.toInt() - 1
                            if (tmp in 0 until history!!.size) {
                                queryHistory.clear()
//                                queryHistory.add("第${query}轮的记录：(共${queryHistory.size}次)")
                                queryHistory.addAll(history!![tmp] as MutableList<String>)
                                Log.i("queryHistory", queryHistory.toString())
                            } else {
                                queryHistory.clear()
                                queryHistory.add("无记录")
                            }
                        } else {
                            queryHistory.clear()
//                            queryHistory.add("第${history!!.size}轮的记录：(共${queryHistory.size}次)")
                            queryHistory.addAll(history!![history.size - 1] as MutableList<String>)
                        }
                    },
                    placeholder = { Text(text = "请输入要查询的轮次...") },
                    singleLine = true, // 单行文本框
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Blue, // 设置焦点时的指示线颜色
                        unfocusedIndicatorColor = Color.Gray, // 设置非焦点时的指示线颜色
                    ),
                    shape = MaterialTheme.shapes.extraSmall, // 设置边框形状
                    textStyle = TextStyle.Default.copy(color = Color.Black), // 设置文本颜色
                )
            }


            queryHistory.forEach {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = it)
                }
            }


        }
    }
}



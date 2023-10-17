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
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
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
        var history = intent.getSerializableExtra("history", ArrayList::class.java)!!
        setContent {
            HistoryScreen(history)
        }
    }
}

@Composable
fun HistoryScreen(history: ArrayList<*>) {
    var query = remember { mutableStateOf("") }
    var queryHistory = remember { mutableListOf<String>() }
    // 初始化显示
    if (query.value == "") {
        queryHistory.clear()
        queryHistory.addAll(history!![history.size - 1] as MutableList<String>)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray)
    ) {
        // 设置为可滚动
        Column(Modifier.verticalScroll(rememberScrollState())) {
            // 返回按钮
            ReturnBtn()

            // 文本框
            QueryText(query, history, queryHistory)

            // 查询的轮次的总体信息

            // 显示轮次
            var turn = query.value
            if (turn == "") {
                turn = history?.size.toString()
            }
            ListItem("第${turn}轮：（共${queryHistory.size}次）", Color.LightGray)
            // 记录为空时的判断
            if (queryHistory.size == 0)
                ListItem("无记录", bgColor = Color.White)
            // 查询的轮次的具体信息
            queryHistory.forEach {
                ListItem(it, Color.White)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueryText(
    query: MutableState<String>,
    history: ArrayList<*>?,
    queryHistory: MutableList<String>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(10.dp, shape = CircleShape)
    ) {
        TextField(
            value = query.value,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = {
                // 输入框改变时...回调
                query.value = it
                // 清空原来的数
                queryHistory.clear()
                if (query.value != "") {
                    val tmp = query.value.toInt() - 1
                    if (tmp in 0 until history!!.size) {
                        queryHistory.addAll(history[tmp] as MutableList<String>)
                        Log.i("queryHistory", queryHistory.toString())
                    }
                } else {
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
}

@Composable
fun ReturnBtn() {
    val context = LocalContext.current as Activity
    Row {
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
        ) {
            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "返回")
            Text("返回", fontSize = 15.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun ListItem(item: String, bgColor: Color) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(bgColor)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = item,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
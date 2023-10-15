package com.cyy.exp1.diceGame

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import java.util.ArrayList
import java.util.Objects

class DiceHistoryActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val history = intent.getSerializableExtra("history", ArrayList::class.java)
        setContent() {
            HistoryScreen(history!!)
        }
    }
}

@Composable
fun <T> HistoryScreen(history: ArrayList<T>) {
    val context = LocalContext.current as Activity
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Yellow)
    ) {
        Button(onClick = {
            // 返回GameWin/LoseActivity
            // 为了实现：点击按钮，结束当前意图(返回代码为：RESULT_OK)
            val intent = Intent()
            intent.putExtra("message", "返回")
            // 传递一个意图参数参数
            context.setResult(Activity.RESULT_OK, intent)
            // 结束当前意图(回到过来的地方)
            context.finish()
        }) {
            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "返回")
            Text("返回", fontSize = 30.sp, textAlign = TextAlign.Center)
        }
        Column {
            history.forEach {
                Row(
                ) {
                    Text(text = it.toString())
                }
            }
        }
    }
}
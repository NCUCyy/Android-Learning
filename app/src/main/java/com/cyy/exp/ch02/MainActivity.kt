package com.cyy.exp.ch02

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.integerArrayResource
import androidx.compose.ui.unit.sp
import com.cyy.exp.ui.theme.ExpTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExpTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    // 获取当前活动的上下文
    val context = LocalContext.current
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Column {
            Button(onClick = {
                val intent = Intent(context, FirstActivity::class.java)
                intent.putExtra("data", "来自MainActivity的数据")
                context.startActivity(intent)
            }) {
                Text("跳转到FirstActivity", fontSize = 30.sp)
            }

            Button(onClick = {
                val intent = Intent(context, SecondActivity::class.java)
                intent.putExtra("data", "来自MainActivity的数据")
                context.startActivity(intent)
            }) {
                Text("跳转到SecondActivity", fontSize = 30.sp)
            }
        }
    }
}

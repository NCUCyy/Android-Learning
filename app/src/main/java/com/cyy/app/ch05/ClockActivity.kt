package com.cyy.app.ch05

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import kotlin.concurrent.thread

class ClockActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ClockScreen()
        }
    }
}


@Composable
fun ClockScreen() {
    var timer by remember { mutableStateOf(0) }
    var running by remember { mutableStateOf(true) }
    var display by remember { mutableStateOf("计时器") }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = display, fontSize = 30.sp)
        Row(horizontalArrangement = Arrangement.Center) {
            TextButton(onClick = {
                running = true
                thread {
                    while (running) {
                        Thread.sleep(1000)
                        timer += 1
                        display = "${timer} 秒"
                    }
                }
            }) {
                Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = "play")
                Text("开始")
            }
            TextButton(onClick = {
                running = false
            }) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "pause")
                Text("暂停")
            }
            TextButton(onClick = {
                running = false
                timer = 0
            }) {
                Icon(imageVector = Icons.Filled.Refresh, contentDescription = "refresh")
                Text("停止")
            }
        }
    }
}
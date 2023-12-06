package com.cyy.app.ch06

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ClockActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }
}

//全局变量，在MainScreen作用域外
val timer = mutableIntStateOf(0)

@Composable
fun MainScreen() {
    //函数作用域内
    var runningState = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "计时器", fontSize = 30.sp, color = MaterialTheme.colorScheme.primary)
            Text(text = "${timer.value}秒", fontSize = 24.sp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(modifier = Modifier.width(100.dp),
                    onClick = {
                        runningState.value = true
                        scope.launch {
                            while (runningState.value) {
                                delay(1000)
                                timer.value += 1
                                Log.d("TAG", "${timer} m")
                            }
                        }
                    }) {
                    Row {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            tint = Color.Green,
                            contentDescription = null
                        )
                        Text("计时")
                    }
                }
                IconButton(modifier = Modifier.width(100.dp),
                    onClick = {
                        runningState.value = false
                    }) {
                    Row {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            tint = Color.Green, contentDescription = null
                        )
                        Text("暂停")
                    }
                }
            }
        }
    }
}

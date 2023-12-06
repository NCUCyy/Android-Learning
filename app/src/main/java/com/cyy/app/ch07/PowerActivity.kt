package com.cyy.app.ch07

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

class PowerActivity : ComponentActivity() {
    // 定义成全局变量，因为要在onDestroy中注销！
    private lateinit var receiver: PowerReceiver
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val powerState = mutableStateOf(0)
        handleReceiver(powerState)
        setContent {
            DisplayScreen(powerState = powerState, modifier = Modifier)
        }
    }

    private fun handleReceiver(powerState: MutableState<Int>) {
        receiver = PowerReceiver(powerState)
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_POWER_CONNECTED)
        intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED)
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED)
        registerReceiver(receiver, intentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}

@Composable
fun DisplayScreen(powerState: MutableState<Int>, modifier: Modifier) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "当前的电量为：${powerState.value}", modifier = modifier, fontSize = 40.sp)
    }
}

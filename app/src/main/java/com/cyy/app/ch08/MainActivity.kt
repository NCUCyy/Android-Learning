package com.cyy.app.ch08

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    private var serviceIntent: Intent? = null
    // 用于数据交换
    private val conn = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            TODO("Not yet implemented")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            TODO("Not yet implemented")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DisplayScreen(playAction = ::playMusic, stopAction = ::stopMusic)
        }
    }

    private fun playMusic() {
        serviceIntent = Intent(this, MusicService::class.java)
//        startService(serviceIntent)
        bindService(serviceIntent!!, conn, Context.BIND_AUTO_CREATE)
    }

    private fun stopMusic() {
        if (serviceIntent != null)
//            stopService(serviceIntent)
            unbindService(conn)
    }
}

@Composable
fun DisplayScreen(playAction: () -> Unit, stopAction: () -> Unit) {
    Box(contentAlignment = Alignment.Center) {
        Row(modifier = Modifier.fillMaxWidth()) {
            TextButton(onClick = {
                playAction.invoke()
            }) {
                Text(text = "播放", fontSize = 20.sp)
            }
            TextButton(onClick = {
                stopAction.invoke()
            }) {
                Text(text = "暂停", fontSize = 20.sp)
            }
        }
    }
}
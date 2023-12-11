package com.cyy.app.ch08

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import kotlin.concurrent.thread

class MainActivity : ComponentActivity() {
    private lateinit var serviceIntent: Intent
    private lateinit var conn: ServiceConnection

    // 控制线程B的运行
    var running = false

    // 当前播放到的百分比 = 100 * ( 当前位置 / 总时长 )
    var musicProgress = 0

    // 已经播放的秒数
    var timer = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        serviceIntent = Intent(this, MusicService::class.java)

        setContent {
            val progressState = remember { mutableStateOf(0.0f) }
            val timerState = remember { mutableStateOf("") }


            val handler = object : Handler(Looper.getMainLooper()) {
                override fun handleMessage(msg: Message) {
                    super.handleMessage(msg)
                    if (msg.what == 0x123) {
                        progressState.value = msg.arg1 / 100.toFloat()
                        timerState.value = msg.obj.toString()
                    }
                }
            }
            // 用于数据交换
            conn = object : ServiceConnection {
                override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                    // TODO：注意该函数只在绑定的时候执行一次（注意📢：传递过来的Service对象本身不变，但是它内部的属性一在onBind定义的线程中被修改！！！）
                    // 接收Service传递过来的Binder对象
                    // TODO：（核心思想）其中的两个属性（timer,musicProgress）的值一直在Service中定义的那个thread中每隔一秒被修改！
                    // 这里return得到的这个Binder对象，就是Service中的ProgressBinder对象，因此也会同步被修改！！！
                    val binder = service as MusicService.ProgressBinder
                    // TODO：开启一个新线程B，用来时时刻刻的【读取】ProgressBinder对象中的两个属性当前的值（注意：值在Service中的线程A中被每隔一秒的修改！）
                    thread {
                        while (running) {
                            // 注意这边也需要sleep一下才能正常显示时间
                            Thread.sleep(100)
                            val msg = Message.obtain()
                            musicProgress = binder.getMusicProgress()
                            timer = binder.getTimer()
                            msg.what = 0x123
                            msg.arg1 = binder.getMusicProgress()
                            // 把秒转换为时:分的形式
                            msg.obj = convertTime(binder.getTimer())
                            handler.sendMessage(msg)
                        }
                    }
                }

                override fun onServiceDisconnected(name: ComponentName?) {
                    TODO("Not yet implemented")
                }
            }

            DisplayScreen(
                timerState = timerState,
                progressState = progressState,
                playAction = ::playMusic,
                stopAction = ::stopMusic
            )
        }
    }

    private fun playMusic() {
        // 开启线程B
        running = true
        // 传入上次播放到的时刻
        serviceIntent.putExtra("timer", timer)
        serviceIntent.putExtra("musicProgress", musicProgress)
        // 绑定服务
        bindService(serviceIntent, conn, Context.BIND_AUTO_CREATE)
    }

    private fun stopMusic() {
        // 结束线程B
        running = false
        // 解绑服务
        unbindService(conn)
    }

    private fun convertTime(seconds: Int): String {
        val m = seconds / 60
        val s = seconds % 60
        val ms = if (m < 10) "0$m" else "$m"
        val ss = if (s < 10) "0$s" else "$s"
        return "${ms}:${ss}"
    }
}

@Composable
fun DisplayScreen(
    timerState: MutableState<String>,
    progressState: MutableState<Float>,
    playAction: () -> Unit,
    stopAction: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = timerState.value, fontSize = 24.sp)
            LinearProgressIndicator(progress = progressState.value)
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
}
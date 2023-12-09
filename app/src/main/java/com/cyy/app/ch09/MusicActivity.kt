package com.cyy.app.ch09

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Build
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
import androidx.core.app.ActivityCompat
import kotlin.concurrent.thread

/**
 * TODO：广播 + 前台服务 的使用
 * TODO：1、广播的使用
 * ：在MusicActivity中注册广播接收器（playReceiver，stopReceiver），用于接收来自通知栏的点击事件（play，stop）
 * 在服务中设置发送广播，会被MusicActivity中接收到
 * 注意：真正的play和stop操作实际上就是两个函数：playMusic()和stopMusic()————操作统一
 * 接收到对应的广播后，分别执行playMusic()和stopMusic()这两个函数，实现播放和暂停
 *
 * TODO：2、三个thread的作用
 * 1. MusicService中的thread-1：用于更新进度条的【raw数据————timer 和 musicProgress 的最直接的来源】
 * 2. MusicActivity中的thread-2：用于实时"监测"Binder对象中属性的变化，来更新界面的UI（从thread-1获取的数据）
 * 3. 前台服务中的thread-3：用于更新通知栏的UI（从thread-1获取的数据）
 */
class MusicActivity : ComponentActivity() {
    private lateinit var serviceIntent: Intent
    private lateinit var conn: ServiceConnection

    // 为了unregister
    private lateinit var playReceiver: BroadcastReceiver
    private lateinit var stopReceiver: BroadcastReceiver

    // 控制线程B的运行
    var running = false

    // TODO：当前播放到的百分比 = 100 * ( 当前位置 / 总时长 )————用于从指定位置重新播放
    var musicProgress = 0

    // TODO：已经播放的秒数————用于从指定位置重新播放
    var timer = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 创建服务意图
        serviceIntent = Intent(this, MusicService::class.java)
        // 请求通知
        requestNotificationPermission()
        // 注册广播接收器
        registerBroadCastReceiver()

        setContent {
            // 界面的状态值
            val progressState = remember { mutableStateOf(0.0f) }
            val timerState = remember { mutableStateOf("") }

            val handler = object : Handler(Looper.getMainLooper()) {
                override fun handleMessage(msg: Message) {
                    super.handleMessage(msg)
                    if (msg.what == 0x123) {
                        // 更新界面状态值————UI更新
                        progressState.value = msg.arg1 / 100.toFloat()
                        timerState.value = msg.obj.toString()
                    }
                }
            }
            // 用于数据交换
            conn = object : ServiceConnection {
                override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                    val binder = service as MusicService.ProgressBinder
                    thread {
                        while (running) {
                            // 注意这边也需要sleep一下才能正常显示时间，不然状态更新太快，UI显示不过来
                            Thread.sleep(10)
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

    private fun registerBroadCastReceiver() {
        // TODO：注册playAction广播接收器
        val playIntentFilter = IntentFilter()
        playIntentFilter.addAction("PLAT_ACTION")
        playReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                playMusic()
            }
        }
        registerReceiver(playReceiver, playIntentFilter, RECEIVER_EXPORTED)

        // TODO：注册stopAction广播接收器
        val stopIntentFilter = IntentFilter()
        stopIntentFilter.addAction("STOP_ACTION")
        stopReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                stopMusic()
            }
        }
        registerReceiver(stopReceiver, stopIntentFilter, RECEIVER_EXPORTED)
    }

    /**
     * 请求通知权限
     */
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                0
            )
        }
    }

    private fun playMusic() {
        if (!running) {
            // 开启线程B
            running = true
            // 传入上次播放到的时刻
            serviceIntent.putExtra("timer", timer)
            serviceIntent.putExtra("musicProgress", musicProgress)
            // 绑定服务
            bindService(serviceIntent, conn, Context.BIND_AUTO_CREATE)
        }
    }

    private fun stopMusic() {
        if (running) {
            // 结束线程B
            running = false
            // 解绑服务
            unbindService(conn)
        }
    }

    private fun convertTime(seconds: Int): String {
        val m = seconds / 60
        val s = seconds % 60
        val ms = if (m < 10) "0$m" else "$m"
        val ss = if (s < 10) "0$s" else "$s"
        return "${ms}:${ss}"
    }

    override fun onDestroy() {
        super.onDestroy()
        // 取消注册广播接收器！
        unregisterReceiver(playReceiver)
        unregisterReceiver(stopReceiver)
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
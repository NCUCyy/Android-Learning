package com.cyy.app.ch09

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.cyy.app.R


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //请求权限
        requestNotificationPermission()
        setContent {
            MainScreen(sendAction = ::showNotification)
        }
    }

    private fun requestNotificationPermission() {
        // 检查通知权限是否已经授予 注意：API 33以上版本需要检查POST_NOTIFICATIONS发布通知权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                0
            )
        }
    }

    private fun showNotification() {
        //定义通知管理器
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //定义通知渠道的标识
        val channelId = "com.cyy.app.ch09"
        //定义通知渠道的名称
        val channelName = "移动应用开发"
        //定义通知渠道:指定通知渠道的标识、名称和通知渠道的重要级别
        val channel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
        //定义通知渠道的描述信息
        val channelDesc = "移动应用开发通知渠道描述"
        //创建并配置通知渠道
        notificationManager.createNotificationChannel(channel)

        //创建通知
        val notification =
            Notification.Builder(this, channelId)
                .apply {
                    //设置通知标题
                    setContentTitle("通知实例一")
                    //设置通知内容
                    setContentText("欢迎使用通知")
                    //设置通知时间
                    setWhen(System.currentTimeMillis())
                    //设置通知的小图标
                    setSmallIcon(R.mipmap.ic_launcher_round)
                    //设置通知的大图标
                    setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.img_1))
                    //设置通知样式
                    style =
                        Notification.BigPictureStyle()
                            .bigPicture(BitmapFactory.decodeResource(resources, R.mipmap.img))
                }.build()

        //创建通知标记
        val notificationID = 1
        //发布通知到通知栏,注意从Android13开始发布通知需要配置android.permission.POST_NOTIFICATIONS许可
        notificationManager.notify(notificationID, notification)
    }
}

@Composable
fun MainScreen(sendAction: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        TextButton(onClick = {
            sendAction.invoke()
        }) {
            Text("发送通知", fontSize = 20.sp)
        }
    }
}
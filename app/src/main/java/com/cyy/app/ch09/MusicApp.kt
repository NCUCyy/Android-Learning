package com.cyy.app.ch09

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class MusicApp : Application() {
    override fun onCreate() {
        super.onCreate()
        //从API 26开始使用通知渠道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //定义通知管理器
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            //定义通知渠道的标识
            val channelId = "com.cyy.app.ch09"
            //定义通知渠道的名称
            val channelName = "移动应用开发"
            //定义通知渠道:指定通知渠道的标识、名称和通知渠道的重要级别
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            //创建并配置通知渠道
            notificationManager.createNotificationChannel(channel)
        }
    }
}

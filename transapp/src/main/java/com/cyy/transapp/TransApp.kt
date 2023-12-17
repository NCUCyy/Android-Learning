package com.cyy.transapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.cyy.transapp.network.SerializationConverter
import com.cyy.transapp.repository.ListenRepository
import com.cyy.transapp.repository.PlanRepository
import com.cyy.transapp.repository.SentenceRepository
import com.cyy.transapp.repository.StarWordRepository
import com.cyy.transapp.repository.TodayRepository
import com.cyy.transapp.repository.TransRepository
import com.cyy.transapp.repository.UserRepository
import com.drake.net.BuildConfig
import com.drake.net.NetConfig
import com.drake.net.okhttp.setConverter
import com.drake.net.okhttp.setDebug
import java.util.concurrent.TimeUnit

class TransApp : Application() {
    // 通过 lazy，数据库和存储库只在需要时创建，而不是在应用程序启动时创建
    private val database by lazy { AppDataBase.getDatabase(this) }


    val transRepository by lazy { TransRepository(database.getTransRecordDao()) }
    val sentenceRepository by lazy { SentenceRepository() }
    val listenRepository by lazy { ListenRepository() }
    val userRepository by lazy { UserRepository(database.getUserDao()) }
    val starWordRepository by lazy { StarWordRepository(database.getStarWordDao()) }
    val planRepository by lazy { PlanRepository(database.getPlanDao()) }
    val todayRepository by lazy { TodayRepository(database.getTodayDao()) }

    override fun onCreate() {
        super.onCreate()
        // Net设置
        NetConfig.initialize("", this) {
            // 超时配置, 默认是10秒, 设置太长时间会导致用户等待过久
            connectTimeout(30, TimeUnit.SECONDS)
            readTimeout(30, TimeUnit.SECONDS)
            writeTimeout(30, TimeUnit.SECONDS)
            setDebug(BuildConfig.DEBUG)
            setConverter(SerializationConverter())
        }
        // 通知设置
        //从API 26开始使用通知渠道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //定义通知管理器
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            //定义通知渠道的标识
            val channelId = "com.cyy.transapp"
            //定义通知渠道的名称
            val channelName = "翻译应用"
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
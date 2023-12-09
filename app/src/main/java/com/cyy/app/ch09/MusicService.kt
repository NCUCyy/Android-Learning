package com.cyy.app.ch09

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.cyy.app.R
import kotlin.concurrent.thread

class MusicService : Service() {
    private lateinit var mediaPlayer: MediaPlayer


    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer.create(this, R.raw.hcy)
    }

    override fun onBind(intent: Intent): IBinder? {
        postNotification()
        playMusic()
        return null
    }

    override fun onUnbind(intent: Intent?): Boolean {
        stopMusic()
        return super.onUnbind(intent)
    }

    private fun playMusic() {
        mediaPlayer.setOnPreparedListener {
            mediaPlayer.start()
        }
        mediaPlayer.setOnCompletionListener {
            mediaPlayer.release()
        }
    }

    private fun stopMusic() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
    }

    /**
     * Request notification permission
     * 请求通知权限
     */
    private fun requestNotificationPermission() {
        val notificationPermissionGranted =
            NotificationManagerCompat.from(this).areNotificationsEnabled()

        if (!notificationPermissionGranted) {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            startActivity(intent)
        }
    }

    private fun postNotification() {
        //请求通知权限
        requestNotificationPermission()
        //创建通知管理器
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //定义通知渠道
        val channel =
            NotificationChannel(
                "music_service",
                "国王与乞丐",
                NotificationManager.IMPORTANCE_DEFAULT
            )
        //创建通知渠道
        notificationManager.createNotificationChannel(channel)
        //定义启动服务的意图
        val intent1 = Intent(this, MusicActivity::class.java)
        //定义悬浮意图
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            intent1, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        //创建通知
        val notification = NotificationCompat.Builder(this, "music_service").apply {
            setOngoing(true)
            setOnlyAlertOnce(true)
            setContentTitle("播放音乐")
            setContentText("正在歌曲播放一剪梅...")
            setSmallIcon(R.mipmap.ic_launcher)
            setColorized(true)
            color = resources.getColor(R.color.teal_200, null)
            setContentIntent(pendingIntent)
        }.build()

        startForeground(1, notification)
    }
}
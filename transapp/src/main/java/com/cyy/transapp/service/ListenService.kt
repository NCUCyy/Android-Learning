package com.cyy.transapp.service

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.cyy.transapp.R
import com.cyy.transapp.pojo.ListenResource
import kotlin.concurrent.thread

class ListenService : Service() {
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var notificationBuilder: NotificationCompat.Builder

    // 音频资源（int类型）
    private lateinit var resource: ListenResource

    // 控制暂停和播放
    private var running = true

    // raw data
    var timer = 0
    var musicProgress = 0

    inner class ProgressBinder : Binder() {
        fun getMusicProgress() = musicProgress
        fun getTimer() = timer
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationBuilder()
    }

    override fun onBind(intent: Intent): IBinder? {
        // 获取资源
        resource = intent.getParcelableExtra("listenResource", ListenResource::class.java)!!
        // 获取上次播放到的位置
        musicProgress = intent.getIntExtra("musicProgress", 0)
        timer = intent.getIntExtra("timer", 0)

        mediaPlayer = MediaPlayer.create(this, resource.mp3)
        notificationBuilder.setContentText("正在播放听力：${resource.topic}")
        postNotification()
        playMusic()
        return ProgressBinder()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        stopMusic()
        return super.onUnbind(intent)
    }

    private fun playMusic() {
        running = true
        mediaPlayer.setOnPreparedListener {
            mediaPlayer.seekTo(musicProgress * (mediaPlayer.duration) / 100)
            mediaPlayer.start()
        }
        mediaPlayer.setOnCompletionListener {
            mediaPlayer.release()
        }
        // 更新进度条
        changeProgress()
        // 刷新通知栏
        postNotification()
    }


    private fun changeProgress() {
        thread {
            while (running) {
                Thread.sleep(1000)
                timer++
                musicProgress = (100 * mediaPlayer.currentPosition) / mediaPlayer.duration
                if (musicProgress == 100) {
                    running = false
                }
            }
        }
    }

    private fun stopMusic() {
        running = false
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
    }

    private fun createNotificationBuilder() {
        val playPendingIntent = getPlayPendingIntent()
        val stopPendingIntent = getStopPendingIntent()
        val descPendingIntent = getDescPendingIntent()
        val playAction =
            NotificationCompat.Action(android.R.drawable.ic_media_play, "播放", playPendingIntent)
        val stopAction =
            NotificationCompat.Action(android.R.drawable.ic_media_pause, "暂停", stopPendingIntent)
        notificationBuilder = NotificationCompat.Builder(this, "com.cyy.transapp").apply {
            setOngoing(true)
            setOnlyAlertOnce(true)
            setWhen(System.currentTimeMillis())
            setContentTitle("每日听力")
//            setContentText("正在播放听力")
            setSmallIcon(R.drawable.listen_resource)
            setColorized(true)
            color = resources.getColor(R.color.teal_200, null)
            setDefaults(NotificationCompat.DEFAULT_SOUND or NotificationCompat.DEFAULT_LIGHTS or NotificationCompat.DEFAULT_VIBRATE)
            setAutoCancel(true)

            setContentIntent(descPendingIntent)
            addAction(playAction)
            addAction(stopAction)
        }
    }


    @SuppressLint("MissingPermission")
    private fun postNotification() {
        // 发送通知
        NotificationManagerCompat.from(this).apply {
            notificationBuilder.setProgress(100, 0, false)
            notify(1, notificationBuilder.build())
            thread {
                while (running) {
                    notificationBuilder.setProgress(100, musicProgress, false)
                    Thread.sleep(1000)
                    // 刷新原有通知
                    notify(1, notificationBuilder.build())
                }
                notificationBuilder
                    .setContentText("已暂停")
                    .setProgress(0, 0, false)
                notify(1, notificationBuilder.build())
            }
        }
    }

    /**
     * 获得播放意图---悬挂意图
     */
    private fun getPlayPendingIntent(): PendingIntent {
        val intent = Intent("PLAT_ACTION")
        return PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    /**
     * 获得停止意图---悬挂意图
     */
    private fun getStopPendingIntent(): PendingIntent {
        val intent = Intent("STOP_ACTION")
        return PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    /**
     * 获得详情意图---点击通知意图
     */
    private fun getDescPendingIntent(): PendingIntent {
        //定义启动服务的意图
        val intent = Intent(this, this::class.java)
        //定义PendingIntent
        return PendingIntent.getActivity(
            this, 1,
            intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }
}
package com.cyy.app.ch09

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.cyy.app.R
import kotlin.concurrent.thread

class MusicService : Service() {
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var notificationBuilder: NotificationCompat.Builder

    private var running = true
    var timer = 0
    var musicProgress = 0

    inner class ProgressBinder : Binder() {
        fun getMusicProgress() = musicProgress
        fun getTimer() = timer
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationBuilder()
        mediaPlayer = MediaPlayer.create(this, R.raw.hcy)
    }

    override fun onBind(intent: Intent): IBinder? {
        musicProgress = intent.getIntExtra("musicProgress", 0)
        timer = intent.getIntExtra("timer", 0)
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
        changeProgress()
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

        notificationBuilder = NotificationCompat.Builder(this, "com.cyy.app.ch09").apply {
            setOngoing(true)
            setOnlyAlertOnce(true)
            setWhen(System.currentTimeMillis())
            setContentTitle("播放音乐")
            setContentText("正在歌曲播放一剪梅...")
            setSmallIcon(R.mipmap.ic_launcher)
            setColorized(true)
            color = resources.getColor(R.color.teal_200, null)
            setDefaults(NotificationCompat.DEFAULT_SOUND or NotificationCompat.DEFAULT_LIGHTS or NotificationCompat.DEFAULT_VIBRATE)
            setAutoCancel(true)

            addAction(R.mipmap.play, "播放", playPendingIntent)
            addAction(R.mipmap.pause, "暂停", stopPendingIntent)
        }
    }

    @SuppressLint("MissingPermission")
    private fun postNotification() {
        // 发送通知
        NotificationManagerCompat.from(this).apply {
            notificationBuilder.setProgress(100, 0, false)
            notify(1, notificationBuilder.build())
            thread {
                var currentProgress = 0
                while (running) {
                    notificationBuilder.setProgress(100, currentProgress, false)
                    Thread.sleep(1000)
                    currentProgress = musicProgress
                    // 刷新原有通知
                    notify(1, notificationBuilder.build())
                }
                notificationBuilder
                    .setContentText("播放完成")
                    .setProgress(0, 0, false)
                notify(1, notificationBuilder.build())
            }
        }
    }

    private fun getPlayPendingIntent(): PendingIntent {
        val intentFilter = IntentFilter()
        intentFilter.addAction("PLAT_ACTION")
        val playActionReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                playMusic()
            }
        }
        registerReceiver(playActionReceiver, intentFilter, RECEIVER_EXPORTED)
        val intent = Intent("PLAT_ACTION")
        return PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun getStopPendingIntent(): PendingIntent {
        val intentFilter = IntentFilter()
        intentFilter.addAction("STOP_ACTION")
        val playActionReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                stopMusic()
            }
        }
        registerReceiver(playActionReceiver, intentFilter, RECEIVER_EXPORTED)
        val intent = Intent("STOP_ACTION")
        return PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}
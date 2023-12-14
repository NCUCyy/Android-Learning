package com.cyy.transapp.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder

class VoiceService : Service() {
    private lateinit var mediaPlayer: MediaPlayer
    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val url = intent!!.getStringExtra("url")
        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            prepare()
            setOnPreparedListener {
                start()
            }
            setOnCompletionListener {
                release()
                // 播放结束后停止服务
                stopSelf()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }


    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
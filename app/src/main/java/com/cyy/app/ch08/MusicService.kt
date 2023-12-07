package com.cyy.app.ch08

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import com.cyy.app.R

class MusicService : Service() {
    private lateinit var mediaPlayer: MediaPlayer
    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer.create(this, R.raw.hcy)
    }

    /**
     * 方式一：onStartCommand()
     */
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        mediaPlayer.setOnPreparedListener {
//            mediaPlayer.start()
//        }
//        mediaPlayer.setOnCompletionListener {
//            mediaPlayer.release()
//            stopSelf()
//        }
//        return super.onStartCommand(intent, flags, startId)
//    }

    /**
     * 方式二：onBind()
     */
    override fun onBind(intent: Intent): IBinder? {
        mediaPlayer.setOnPreparedListener {
            mediaPlayer.start()
        }
        mediaPlayer.setOnCompletionListener {
            mediaPlayer.release()
            stopSelf()
        }
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
    }
}
package com.cyy.app.ch08

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import com.cyy.app.R
import kotlinx.coroutines.runInterruptible
import kotlin.concurrent.thread

class MusicService : Service() {
    private lateinit var mediaPlayer: MediaPlayer
    var running = true
    var timer = 0
    var musicProgress = 0

    inner class ProgressBinder : Binder() {
        fun getMusicProgress() = musicProgress
        fun getTimer() = timer
    }

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
    override fun onBind(intent: Intent): IBinder {
        mediaPlayer.setOnPreparedListener {
            mediaPlayer.start()
        }
        mediaPlayer.setOnCompletionListener {
            mediaPlayer.release()
            timer = 0
        }
        thread {
            while (running) {
                Thread.sleep(1000)
                timer++
                musicProgress = (100 * mediaPlayer.currentPosition) / mediaPlayer.duration
                if (musicProgress >= 100)
                    running = false
            }
        }
        return ProgressBinder()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        running = false
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        return super.onUnbind(intent)
    }
}
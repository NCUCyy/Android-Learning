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

    // 控制线程A的运行
    private var running = true

    // 用于包装进Binder对象中传递给MainActivity
    var timer = 0
    var musicProgress = 0

    // TODO：定义一个内部类，用于包装需要传递给MainActivity的数据
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
        // TODO：注意该函数只在绑定的时候执行一次
        mediaPlayer.setOnPreparedListener {
            mediaPlayer.start()
        }
        mediaPlayer.setOnCompletionListener {
            mediaPlayer.release()
            timer = 0
        }
        // TODO：开启一个新线程A：用来【改变】ProgressBinder对象中的两个属性
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
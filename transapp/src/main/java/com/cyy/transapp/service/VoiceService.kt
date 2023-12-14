package com.cyy.transapp.service

import android.app.Service
import android.content.Intent
import android.os.IBinder

class VoiceService : Service() {

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
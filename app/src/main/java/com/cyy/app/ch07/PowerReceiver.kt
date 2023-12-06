package com.cyy.app.ch07

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.widget.Toast
import androidx.compose.runtime.MutableState

class PowerReceiver(val powerState: MutableState<Int>) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_POWER_CONNECTED -> {
                showInfo(context, "开始充电")
            }

            Intent.ACTION_POWER_DISCONNECTED -> {
                showInfo(context, "充电结束")
            }
        }
        powerState.value = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
    }

    private fun showInfo(context: Context, info: String) {
        Toast.makeText(context, info, Toast.LENGTH_SHORT).show()
    }
}


package com.cyy.app.ch07

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

class MyReceiver02 : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val msg = intent.getStringExtra("msg")!!
        // 接收数据
        val data = getResultExtras(true) // 接收语句
        val receivedData = data.getString("data")
        showInfo(context, "MyReceiver02接收到的数据：${msg}-${receivedData}")

        // 发送数据
        val sendData = Bundle()
        sendData.putString("data", "来自MyReceiver02的数据")
        setResultExtras(sendData) // 发送语句
    }

    private fun showInfo(context: Context, info: String) {
        Toast.makeText(context, info, Toast.LENGTH_SHORT).show()
    }
}

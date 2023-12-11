package com.cyy.app.ch07

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

class MainActivity : ComponentActivity() {
    // 因为要在onDestroy()中注销广播接收器，所以需要在全局定义广播接收器
    private lateinit var receiver01: MyReceiver01
    private lateinit var receiver02: MyReceiver02
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1、注册广播
        registerBroadCast()
        setContent {
            // 2、发送广播
            Display(action = ::sendAction)
        }
    }

    private fun registerBroadCast() {
        receiver01 = MyReceiver01()
        receiver02 = MyReceiver02()

        val intentFilter1 = IntentFilter()
        intentFilter1.addAction("receiver-test")
        intentFilter1.priority = 100

        val intentFilter2 = IntentFilter()
        intentFilter2.addAction("receiver-test")
        intentFilter2.priority = 300

        registerReceiver(receiver01, intentFilter1, RECEIVER_EXPORTED)
        registerReceiver(receiver02, intentFilter2, RECEIVER_EXPORTED)
    }

    /**
     * 标准广播和有序广播的区别（示例的大致流程）
     * ①MainActivity发送有序广播：
     * 02先接收到(有msg数据，无received数据)，执行完后，
     * 01接收到(有msg数据，有received数据【来自01的数据】)，执行完后，再回到MainActivity
     *
     * ②MainActivity发送标准广播：
     * 01和02同时收到，所以都只有msg数据，而没有received数据（即：都为null）
     */
    private fun sendAction() {
        val intent = Intent("receiver-test")
        intent.putExtra("msg", "来自MainActivity的广播")
        // 注意：必须设置包名！！！
        intent.setPackage(packageName)
        // 1.标准广播
        sendBroadcast(intent)
        // 2.有序广播
//        sendOrderedBroadcast(intent, null)
    }

    override fun onDestroy() {
        super.onDestroy()
        // 3、销毁广播
        unregisterReceiver(receiver01)
    }
}

@Composable
fun Display(action: () -> Unit) {
    TextButton(
        modifier = Modifier.fillMaxSize(),
        onClick = {
            action.invoke()
        }) {
        Text("发送广播")
    }
}

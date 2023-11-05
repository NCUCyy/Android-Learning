package com.cyy.exp2.jump

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier

class FirstActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i("123123123","132123312")
        // 接收MainActivity传递过来的参数(需要指定参数的类型)
        val data = intent.getStringExtra("data")

        setContent {
            // 把参数值传递给「子组件」进行使用
            CommonScreen(data)
        }
    }
}

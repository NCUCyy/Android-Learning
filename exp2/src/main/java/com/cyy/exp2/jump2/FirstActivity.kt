package com.cyy.exp2.jump2

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier

class FirstActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 接收MainActivity传递过来的参数(需要指定参数的类型)
        val data = intent.getParcelableExtra("data", Teacher::class.java)

        setContent {
            // 把参数值传递给「子组件」进行使用
            CommonScreen(message = data!!)
        }
    }
}
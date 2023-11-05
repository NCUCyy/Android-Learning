package com.cyy.exp2.jump2

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi

class SecondActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 接收MainActivity传递过来的参数(需要指定参数的类型)
        val data = intent.getSerializableExtra("data", Student::class.java)

        setContent {
            // 把参数值传递给「子组件」进行使用
            CommonScreen(message = data!!)
        }
    }
}
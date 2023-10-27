package com.cyy.app.ch01

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.cyy.app.ui.theme.ExpTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp


//Compose方式(不需要修改theme配置)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExpTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting()
                }
            }
        }
    }
}

@Composable
fun Greeting(modifier: Modifier = Modifier) {
    Box(contentAlignment = Alignment.Center) {
        val context = LocalContext.current
        Button(onClick = {
//            定义点击事件
            Toast.makeText(context, "显示第一个Compose", Toast.LENGTH_LONG).show()
//              【隐式意图（需要在Manifest.xml文件中进行配置）】
//            从当前活动（MainActivity）跳转到指定活动（FirstActivity）
            val intent = Intent("cn.edu.ncu.cyy.FirstActivity")
            context.startActivity(intent)
        }) {
//            按钮内部的组件
            Row {
                Text(
                    text = "Hello Android World!",
                    fontSize = 30.sp,
                    modifier = modifier
                )
                Icon(
                    imageVector = Icons.Filled.FavoriteBorder,
                    contentDescription = "空",
                    tint = Color.Red
                )
            }

        }
    }
}

//@Preview
//@Composable
//fun GreetingPreview() {
//    ExpTheme {
//        Greeting()
//    }
//}
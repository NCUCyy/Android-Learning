package com.cyy.exp2.jump

import android.app.Activity
import android.content.Intent
import android.os.Parcelable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp

// 通用Screen
@Composable
fun <T> CommonScreen(data: T) {
    val context = LocalContext.current as Activity
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Yellow)
    ) {
        Text(text = "${data.toString()}", fontSize = 20.sp, maxLines = 2)
        Button(onClick = {
            // 为了实现：点击按钮，结束当前意图(返回代码为：RESULT_OK)
            val intent = Intent()
            intent.putExtra("toMain", "${context.localClassName}返回MainActivity")
            // 传递一个意图参数参数
            context.setResult(Activity.RESULT_OK, intent)
            // 结束当前意图(回到过来的地方)
            context.finish()
        }) {
            Text(text = "返回")

        }
    }
}
package com.cyy.exp1.diceGame

import android.app.Activity
import android.content.ClipDescription
import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cyy.exp1.R

@Composable
fun GameResultScreen(imageId: Int, result: String) {
    val context = LocalContext.current as Activity
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Blue)
    ) {
        Column {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = result, fontSize = 50.sp)
            }
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.height(400.dp)
            ) {
                Image(
                    painter = painterResource(id = imageId),
                    contentDescription = "$result",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(onClick = {
                    // 返回GameActivity
                    // 为了实现：点击按钮，结束当前意图(返回代码为：RESULT_OK)
                    val intent = Intent()
                    intent.putExtra("message", "请继续游戏...")
                    // 传递一个意图参数参数
                    context.setResult(Activity.RESULT_OK, intent)
                    // 结束当前意图(回到过来的地方)
                    context.finish()

                }) {
                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "返回")
                    Text("返回", fontSize = 30.sp, textAlign = TextAlign.Center)
                }
                Button(onClick = {
                    /*TODO*/
                }) {
                    Icon(imageVector = Icons.Filled.List, contentDescription = "游戏历史")
                    Text("游戏历史", fontSize = 30.sp, textAlign = TextAlign.Center)
                }
            }
        }

    }
}

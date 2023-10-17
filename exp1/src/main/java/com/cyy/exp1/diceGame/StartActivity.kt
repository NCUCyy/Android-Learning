package com.cyy.exp1.diceGame

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cyy.exp1.R


class StartActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(modifier = Modifier.fillMaxHeight()) {
            TopMenu()
            Spacer(modifier = Modifier.height(150.dp))
            Image(
                modifier = Modifier
                    .size(400.dp)
                    .clickable {
                        // 设置Image可点击---点击后跳转到GameActivity
                        val intent = Intent(context, GameActivity::class.java)
                        context.startActivity(intent)
                    },
                painter = painterResource(id = R.mipmap.home),
                contentDescription = "首页"
            )

        }
    }
}

@Composable
fun RuleDialog(showRule: MutableState<Boolean>) {
    val context = LocalContext.current as Activity
    // 展示规则
    AlertDialog(
        onDismissRequest = {
            // 点击Dialogue以外的地方时执行的操作
            showRule.value = false
        },
        title = {
            Column {
                Text(text = "游戏规则：", fontSize = 30.sp)
            }
        },
        text = {
            Text(
                """
                     第一次扔：
                     { 7, 11 } -> 赢
                     { 2, 3, 1 } -> 输
                     { 其他 } -> 继续游戏
                     
                     第二/三...次扔：
                     { 7 } -> 输
                     { 点数与上一次的相同 } -> 赢
                     { 其他 } -> 继续游戏
                """.trimIndent(), fontSize = 20.sp
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    // 确定按钮点击时执行的操作
                    showRule.value = false
                }) {
                Text(text = "关闭")
            }
        },
        modifier = Modifier.width(280.dp)
    )


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopMenu() {
    val context = LocalContext.current
    var showRule = remember {
        mutableStateOf(false)
    }
    TopAppBar(title = {
        Row() {
            IconButton(onClick = {
                // 转到首页
                context.startActivity(
                    Intent(
                        context,
                        StartActivity::class.java
                    )
                )
            }) {
                Icon(imageVector = Icons.Default.Home, contentDescription = "rule")
            }
            IconButton(onClick = { showRule.value = true }) {
                Icon(imageVector = Icons.Default.Info, contentDescription = "rule")
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {

            Text(
                text = "抛骰子游戏",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    })
    // 展示规则
    if (showRule.value) {
        RuleDialog(showRule = showRule)
    }
}


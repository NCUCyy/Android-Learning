package com.cyy.transapp.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

class IndexActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 使用ActivityResultLauncher进行意图跳转
        val resultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            // 意图结束后，执行这个「回调函数」
            ActivityResultCallback {
                if (it.resultCode == RESULT_OK) {
                    Toast.makeText(this, "退出登录", Toast.LENGTH_LONG).show()
                }
            }
        )
        setContent {
            IndexScreen(resultLauncher)
        }
    }
}

@Composable
fun IndexScreen(resultLauncher: ActivityResultLauncher<Intent>) {
    val context = LocalContext.current as Activity
    Column {
        Text(
            text = "翻译App",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            fontSize = 40.sp
        )
        Button(onClick = {
            // TODO：跳转到LoginActivity
            val intent = Intent(context, LoginActivity::class.java)
            resultLauncher.launch(intent)
        }) {
            Text(text = "登录", fontWeight = FontWeight.Bold, fontSize = 25.sp)
        }
        Button(onClick = {
            // TODO: 跳转到RegisterActivity
            val intent = Intent(context, RegisterActivity::class.java)
            resultLauncher.launch(intent)
        }) {
            Text(text = "注册", fontWeight = FontWeight.Bold, fontSize = 25.sp)
        }
    }
}
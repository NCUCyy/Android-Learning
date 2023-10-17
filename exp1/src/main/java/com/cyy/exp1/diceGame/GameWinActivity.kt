package com.cyy.exp1.diceGame

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import com.cyy.exp1.R

class GameWinActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 使用ActivityResultLauncher进行意图跳转
        val resultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            // 意图结束后，执行这个「回调函数」
            ActivityResultCallback {
                if (it.resultCode == Activity.RESULT_OK) {
                    // 返回的data数据是个intent类型，里面存储了一段文本内容
                    val text = it.data?.getStringExtra("message")
                    Toast.makeText(this, "接受：$text", Toast.LENGTH_LONG).show()
                }
            }
        )
        setContent {
            GameResultScreen(imageId = R.mipmap.happy, intent, resultLauncher, Color.Yellow)
        }
    }


}


package com.cyy.exp2.jump


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat.finishAffinity
import kotlinx.coroutines.launch
import kotlin.system.exitProcess


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 使用ActivityResultLauncher进行意图跳转
        val resultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            // 意图结束后，执行这个「回调函数」
            ActivityResultCallback {
                if (it.resultCode == Activity.RESULT_OK) {
                    // 返回的data数据是个intent类型，里面存储了一段文本内容
                    val text = it.data?.getStringExtra("toMain")
                    Toast.makeText(this, "接受：$text", Toast.LENGTH_LONG).show()
                }
            }
        )
        setContent {
            // 要把这个resultLauncher一直传递到Button手里
            Menu(resultLauncher = resultLauncher)
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun Menu(resultLauncher: ActivityResultLauncher<Intent>) {
    Scaffold(
        //定义头部
        topBar = {
            // 定义顶部栏需要解决两个问题：
            // （1）需要在顶部栏定义顶部的右侧导航菜单
            // （2）需要定义顶部的导航按钮，使得启动侧滑菜单
            TopAppBar(
                // 左侧文本
                title = {
                    Text("按钮菜单")
                },
            )
        }, content = {
            MainScreen(resultLauncher = resultLauncher)
        }
    )
}

@Composable
fun MainScreen(resultLauncher: ActivityResultLauncher<Intent>) {
    // 获取当前活动的上下文
    val context = LocalContext.current

    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Column {
            CustomButton(
                title = "跳转到FirstActivity",
                context = context,
                activityType = FirstActivity::class.java,
                resultLauncher = resultLauncher
            )
            CustomButton(
                title = "跳转到SecondActivity",
                context = context,
                activityType = SecondActivity::class.java,
                resultLauncher = resultLauncher
            )
            Button(modifier = Modifier.fillMaxWidth(), onClick = {
                finishAffinity(context as Activity)
                exitProcess(0)
            }) {
                Text(text = "退出应用", fontSize = 30.sp)
            }
        }
    }
}


@Composable
fun <T> CustomButton(
    title: String,
    context: Context,
    activityType: Class<T>,
    resultLauncher: ActivityResultLauncher<Intent>
) {
    Button(modifier = Modifier.fillMaxWidth(), onClick = {
        turnAction(
            context = context,
            activityType = activityType,
            resultLauncher = resultLauncher
        )
    }) {
        Text(title, fontSize = 30.sp)
    }
}

fun <T> turnAction(
    context: Context,
    activityType: Class<T>,
    resultLauncher: ActivityResultLauncher<Intent>
) {
    // 若要跳转活动，就要先创建一个意图（且必须又当前活动的context）
    // 传递参数，需调用函数————putExtra(paramName, paramData)
    val intent = Intent(context, activityType)
    intent.putExtra("data", "来自MainActivity的问候")
    // 使用resultLauncher进行意图跳转（或：context.startActivity(intent)也可以）
    resultLauncher.launch(intent)
}

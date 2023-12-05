package com.cyy.app.ch05

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import com.cyy.app.R
import kotlin.concurrent.thread

class ImageRepository(private val handler: Handler) {
    // 注意：是Int类型！
    val imageList = listOf(
        R.mipmap.img,
        R.mipmap.img_1,
    )

    fun requestImage(imageId: MutableState<Int>) {
        thread {
            //定义工作线程
            while (imageId.value in imageList.indices) {
                Thread.sleep(1000)

                // 创建消息Message对象
                var message = Message.obtain()
                message.what = 0x123
                message.arg1 = imageList[imageId.value]
                imageId.value = imageId.value + 1

                // 发送消息
                handler.sendMessage(message)
            }
        }
    }
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            //图片列表的序号状态
            val imageState = remember { mutableStateOf(0) }
            //定义Handler对象
            val handler = object : Handler(Looper.getMainLooper()) {
                override fun handleMessage(msg: Message) {
                    super.handleMessage(msg)
                    if (msg.what == 0x123) {
                        //接受数据，并修改状态值
                        imageState.value = msg.arg1
                    }
                }
            }
            //定义图片仓库
            val imageRepository = ImageRepository(handler)
            MainScreen(imageState = imageState, imageRepository = imageRepository)
        }
    }
}


@Composable
fun MainScreen(imageState: MutableState<Int>, imageRepository: ImageRepository) {
    var currentSelected = remember { mutableStateOf(0) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (imageState.value != 0)
            Image(painter = painterResource(imageState.value), contentDescription = "image")
        else {
            Text("等待加载图片")
        }

        Row {
            for (i in 0 until imageRepository.imageList.size) {
                RadioButton(selected = currentSelected.value - 1 == i,
                    onClick = {
                        currentSelected.value = i
                    })
            }
        }

        Row {
            Button(onClick = {
                // 启动Thread线程
                imageRepository.requestImage(currentSelected)
            }) {
                Text("动态显示")
            }
            Button(onClick = {
                imageState.value = 0
                currentSelected.value = 0
            }) {
                Text("重置")
            }
        }
    }
}
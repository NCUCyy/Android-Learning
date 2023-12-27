package com.cyy.transapp.activity.other

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.cyy.transapp.R

class SystemSettingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SettingMainScreen()
        }
    }
}

// 背景色
var bgColor = mutableStateOf(Color.White)

// 背景图
var imageUri = mutableStateOf<Uri?>(null)

// 字体（部分元素）
var syncFontSize = mutableStateOf(0f)

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingMainScreen() {
    val context = LocalContext.current as Activity
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // TODO：显示查询的词汇
                    Text(
                        text = "设置",
                        fontWeight = FontWeight.Bold,
                        fontSize = (20 + syncFontSize.value).sp,
                    )
                },
                // 左侧图标
                navigationIcon = {
                    // 图标按钮
                    IconButton(onClick = {
                        // TODO：返回
                        context.finish()
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.back),
                            contentDescription = null,
                        )
                    }
                },
                actions = {
                }
            )
        },
        bottomBar = {
        },
        content = {
            Surface(color = bgColor.value) {
                Box(modifier = Modifier.padding(it)) {
                    SettingScreen()
                }
            }
        },
        floatingActionButton = {
        })
}

/**
 * 1、字体大小
 * 2、护眼模式
 * 3、app信息（版权、功能、思路等）
 */
@Composable
fun SettingScreen() {
    val context = LocalContext.current as Activity
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri.let {
                // 更新头像
                uri.let {
                    imageUri.value = it
                }
                Toast.makeText(context, "修改成功！", Toast.LENGTH_SHORT).show()
            }
        }
    )
    // 用于背景图的显示
    Box(modifier = Modifier.fillMaxSize()) {
        imageUri.value?.let {
            Image(
                painter = rememberAsyncImagePainter(model = imageUri.value),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )
        }
    }
    Column(modifier = Modifier.fillMaxSize()) {
        // 切换背景图
        Button(onClick = {
            galleryLauncher.launch("image/*")
        }) {
            Text(
                text = "点击切换背景图片",
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        // 撤销背景图
        Button(onClick = {
            imageUri.value = null
        }) {
            Text(text = "点击撤销背景图片", style = MaterialTheme.typography.bodyLarge)
        }
        // 护眼模式
        Button(onClick = {
            if (bgColor.value == Color.White)
                bgColor.value = Color(0xFFF6FF98)
            else
                bgColor.value = Color.White
        }) {
            Text(text = "点击切换护眼模式", style = MaterialTheme.typography.bodyLarge)
        }
        Text(text = "字体测试", fontSize = (20 + syncFontSize.value).sp)
        // 字体
        Slider(
            value = syncFontSize.value,
            onValueChange = { newFontSize ->
                syncFontSize.value = newFontSize
                Log.i("fontSize", syncFontSize.value.toString())
            },
            valueRange = -5f..5f,
            steps = 18
        )
    }
}

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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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
                Column(modifier = Modifier.padding(it)) {
                    SettingCard()
                    Spacer(modifier = Modifier.height(30.dp))
                    AppInfo()
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
@Preview
fun SettingCard() {
    val context = LocalContext.current as Activity
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri.let {
                uri.let {
                    imageUri.value = it
                }
                Toast.makeText(context, "修改成功！", Toast.LENGTH_SHORT).show()
            }
        }
    )
    Card(
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, start = 20.dp, end = 20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        )
    ) {
        Text(
            text = "更换背景",
            fontSize = (20 + syncFontSize.value).sp,
            modifier = Modifier.padding(20.dp),
            fontWeight = FontWeight.Bold
        )
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            // 切换背景图
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                modifier = Modifier.padding(
                    start = 20.dp,
                    top = 10.dp,
                    end = 10.dp,
                    bottom = 10.dp
                ),
                elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 10.dp),
                onClick = {
                    galleryLauncher.launch("image/*")
                }) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.gallery),
                        contentDescription = null
                    )
                    Text(
                        text = "选择背景图",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Image(
                painter = rememberAsyncImagePainter(model = imageUri.value),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RectangleShape)
            )
            if (imageUri.value != null) {
                Spacer(modifier = Modifier.width(10.dp))
                Icon(
                    modifier = Modifier.clickable {
                        // 清空背景
                        imageUri.value = null
                    },
                    painter = painterResource(id = R.drawable.cancel_2),
                    contentDescription = null,
                    tint = Color.Red
                )
            }
        }
        // 护眼模式
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = bgColor.value,
                contentColor = Color.Black
            ),
            elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 10.dp),
            modifier = Modifier.padding(start = 20.dp, top = 10.dp),
            enabled = imageUri.value == null,
            onClick = {
                if (bgColor.value == Color(0xFFFFFBD5)) {
                    // 若已打开，则关闭
                    bgColor.value = Color.White
                } else {
                    // 若未打开，则打开
                    bgColor.value = Color(0xFFFFFBD5)
                }
            }) {
            if (bgColor.value == Color(0xFFFFFBD5)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.visibility_off),
                        contentDescription = null
                    )
                    Text(text = "关闭护眼模式", modifier = Modifier.padding(10.dp))
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.visibility),
                        contentDescription = null
                    )
                    Text(text = "打开护眼模式", modifier = Modifier.padding(10.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Divider(
            thickness = 1.dp,
            color = Color(0xFFDDDDDD),
            modifier = Modifier.padding(20.dp)
        )
        Text(
            text = "修改字体",
            fontSize = (20 + syncFontSize.value).sp,
            modifier = Modifier.padding(20.dp),
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "A", fontSize = 15.sp)

            // 字体
            Slider(
                value = syncFontSize.value,
                onValueChange = { newFontSize ->
                    syncFontSize.value = newFontSize
                    Log.i("fontSize", syncFontSize.value.toString())
                },
                valueRange = -5f..5f,
                steps = 18,
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color.LightGray,
                    inactiveTrackColor = Color.LightGray,
                    disabledThumbColor = Color.LightGray,
                    inactiveTickColor = Color.LightGray,
                    disabledActiveTickColor = Color.LightGray,
                    disabledInactiveTickColor = Color.LightGray,
                    activeTickColor = Color.LightGray
                ),
                modifier = Modifier
                    .size(width = 280.dp, height = Dp.Infinity)
                    .padding(bottom = 10.dp)
            )
            Text(text = "A", fontSize = 25.sp)
        }
    }
}

@Composable
fun AppInfo() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.app_logo),
            contentDescription = null,
            modifier = Modifier.size(35.dp),
            tint = Color(0xFF2cb74d)
        )
        Spacer(modifier = Modifier.width(5.dp)) // 添加一些间距
        Text(
            text = "TransApp",
            fontWeight = FontWeight.Bold,
            fontSize = 25.sp,
            textAlign = TextAlign.Center
        )
    }

    Spacer(modifier = Modifier.height(15.dp))
    Text(
        text = "致力于打造一款简约的单词翻译和学习软件",
        modifier = Modifier.fillMaxWidth(),
        fontSize = 18.sp,
        textAlign = TextAlign.Center,
        color = Color.Gray
    )
    Spacer(modifier = Modifier.height(15.dp))
    Text(
        text = "开发者：曹义扬",
        modifier = Modifier.fillMaxWidth(),
        fontSize = 18.sp,
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(10.dp))
    Text(
        text = "联系电话：15157982271",
        modifier = Modifier.fillMaxWidth(),
        fontSize = 18.sp,
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(15.dp))
    Text(
        text = "Copyright © 2023 NCU Edu.",
        modifier = Modifier.fillMaxWidth(),
        fontSize = 12.sp,
        textAlign = TextAlign.Center,
        color = Color.Gray
    )
}
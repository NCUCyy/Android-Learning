package com.cyy.exp2.memo

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.time.LocalDateTime



class MemoEditActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MemoScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                // 左侧文本
                title = {
                    Text("退出编辑")
                },
                navigationIcon = {
                    // 图标按钮
                    IconButton(onClick = {
                        // 点击按钮返回主活动：保存并返回
                        // TODO
                    }) {
                        Icon(
                            imageVector = Icons.Filled.List,
                            contentDescription = "弹出侧滑菜单"
                        )
                    }
                },
            )
        },
        content = {

            // 页面的主体部分
            Box(modifier = Modifier.padding(it)) {
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                // 结束编辑：保存并返回
                // TODO
            }) {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "返回"
                )
            }
        })
}
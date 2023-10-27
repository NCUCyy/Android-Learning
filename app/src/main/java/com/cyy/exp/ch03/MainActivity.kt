package com.cyy.exp.ch03

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.estimateAnimationDurationMillis
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlin.concurrent.timer
import kotlin.math.exp


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
        }
    }
}

val screens = listOf(Screen.Home, Screen.Setting, Screen.Help)

// 主界面
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun MainScreen() {
    // 记录：当前屏幕
    val currentScreen = remember { mutableStateOf<Screen>(Screen.Home) }
    // 记录：是否打开下拉框
    val dropState = remember { mutableStateOf(false) }
    // 记录：侧滑菜单的状态（两个值）---DrawerValue.Closed、DrawerValue.Open
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    // Coroutine---协程：启动协程scope.launch()
    val scope = rememberCoroutineScope()
    // 记录：信息提示框是否打开
    val displayedSnackState = remember { mutableStateOf(false) }


    // 页面骨架的脚手架
    Scaffold(
        //定义头部
        topBar = {
            TopAppBar(
                // 左侧文本
                title = {
                    Text("侧滑菜单")
                },
                // 左侧图标
                navigationIcon = {
                    // 图标按钮
                    IconButton(onClick = {
                        // 点击按钮，开启异步操作---协程
                        if (drawerState.currentValue == DrawerValue.Closed) {
                            // 当前为关闭：当用户点击时，打开drawer
                            scope.launch {
                                drawerState.open()
                            }
                        } else {
                            // 当前为打开：当用户点击时，关闭drawer
                            scope.launch {
                                drawerState.close()
                            }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Filled.List,
                            contentDescription = "弹出侧滑菜单"
                        )
                    }
                },
                // 右侧按钮————按行处理的交互
                actions = {
                    IconButton(onClick = {
                        dropState.value = !dropState.value
                    }) {
                        Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "More...")
                        if (dropState.value)
                            MenuView(currentScreen, dropState)
                    }
                })
        },
        //定义底部导航
        bottomBar = {
            BottomView(currentScreen = currentScreen)
        },
        //定义悬浮按钮
        floatingActionButton = {
            FloatingActionButton(onClick = {
                currentScreen.value = Screen.Home
                displayedSnackState.value = !displayedSnackState.value
            }) {
                Icon(imageVector = Icons.Filled.Home, contentDescription = "返回")
            }
        },//定义信息提示区
        snackbarHost = {
            if (displayedSnackState.value) {
                Snackbar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Blue),
                ) {
                    Text("提示信息:返回首页", fontSize = 24.sp)
                }
            }
        },
        // 主体
        content = {
            // 主界面以及抽屉界面的定义
            /**
             * 关于DrawerState(open/closed) 和 Coroutine(协程)：
             * 1、注意在MainScreen中的Scaffold的中心区修改为调用drawerView组合函数，并使用DrawerState状态值控制侧滑菜单的启动和关闭
             * - 通过调用drawerState的open函数和close函数分别实现。
             * 2、因为drawerState的open函数和close函数均为suspend挂起函数，需要在协程中运行；
             * - 因此还增加了一个scope的参数，用它来加载drawerState的open函数和close函数。
             */
            DrawView(
                currentScreen = currentScreen,
                drawerState = drawerState,
                scope = scope
            )
        },
    )
}

// 底部导航栏
@Composable
fun BottomView(currentScreen: MutableState<Screen>) {
    BottomAppBar {
        screens.forEach {
            NavigationBarItem(
                // 选中的按钮被高亮显示
                selected = currentScreen.value.route == it.route,
                onClick = {
                    //定义点击动作:切换当前页面
                    currentScreen.value = it
                },
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = it.icon,
                            tint = Color.Blue,
                            contentDescription = it.title
                        )
                        Text(text = it.title, fontSize = 20.sp)
                    }
                })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawView(
    currentScreen: MutableState<Screen>,
    drawerState: DrawerState,
    scope: CoroutineScope
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            // 抽屉的配置
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(360.dp)
                    .background(Color.White)
            ) {
                // 抽屉中要显示的内容
                screens.forEach {
                    NavigationDrawerItem(
                        label = {
                            Text(it.title, fontSize = 30.sp)
                        },
                        icon = {
                            Icon(
                                imageVector = it.icon,
                                tint = Color.Green,
                                contentDescription = it.title
                            )
                        },
                        // 选中的按钮被高亮显示
                        selected = it.route == currentScreen.value.route,
                        onClick = {
                            scope.launch {
                                currentScreen.value = it
                                drawerState.close()
                            }
                        })
                }
            }
        },
        // 主屏幕的内容：currentScreen中标记的页面
        content = {
            currentScreen.value.loadScreen()
        })

}

// 下拉菜单
@Composable
fun MenuView(currentScreen: MutableState<Screen>, dropState: MutableState<Boolean>) {
    DropdownMenu(expanded = dropState.value,
        onDismissRequest = {
            // 点击其他地方，则关闭下拉框
            dropState.value = false
        }) {
        screens.forEach {
            DropdownMenuItem(
                // 在前面的Icon
                leadingIcon = {
                    Icon(imageVector = it.icon, contentDescription = it.title)
                },
                text = {
                    Text(text = it.title, fontSize = 20.sp)
                }, onClick = {
                    // 修改当前屏幕为...
                    currentScreen.value = it
                    // 点击完之后，关闭下拉框
                    dropState.value = false
                })
        }
    }
}

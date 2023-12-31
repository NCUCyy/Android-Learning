package com.cyy.app.ch03

import android.R
import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * 可以发现MainScreen中定义了很多的状态值，这些状态值往往需要作为函数的参数进行传递，处理过程复杂，可以对这些状态值做一个优化处理。
 * 首先，定义一个类，保存各种需要的状态。
 */
@OptIn(ExperimentalMaterial3Api::class)
class StateHolder(
    val currentScreen: MutableState<Screen>,
    val dropState: MutableState<Boolean>,
    val drawerState: DrawerState,
    val displayedSnackState: MutableState<Boolean>,
    val scope: CoroutineScope
)

/**
 * 然后再定义一个组合函数获取所有的状态值
 * - 注意必须是@Composable函数，因为需要使用remember{...}
 * - 目的：给StateHolder中的所有属性普通MutableState对象套一层remember{} ===> 可记忆
 * 1、函数参数的默认值：相当于这些状态的初始值
 * 2、函数的返回值是：StateHolder对象，存储所有的State状态
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberStates(
    currentScreen: MutableState<Screen> = remember { mutableStateOf(Screen.Home) },
    dropState: MutableState<Boolean> = remember { mutableStateOf(false) },
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    displayedSnackState: MutableState<Boolean> = remember { mutableStateOf(false) },
    scope: CoroutineScope = rememberCoroutineScope(),
) = StateHolder(currentScreen, dropState, drawerState, displayedSnackState, scope)

// 在此前提下定义一个保存要显示界面的列表
val screens = listOf(Screen.Home, Screen.Setting, Screen.Help)

// 主界面
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun MainScreen() {
    // 记录：当前屏幕
//    val currentScreen = remember { mutableStateOf<Screen>(Screen.Home) }
//    // 记录：是否打开下拉框
//    val dropState = remember { mutableStateOf(false) }
//    // 记录：侧滑菜单的状态（两个值）---DrawerValue.Closed、DrawerValue.Open
//    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
//    // Coroutine---协程：启动协程scope.launch()
//    val scope = rememberCoroutineScope()
//    // 记录：信息提示框是否打开
//    val displayedSnackState = remember { mutableStateOf(false) }

    // 包裹所有状态
    // 通过这样的方式，单一传递状态值在不同的组合函数共享。
    val states = rememberStates()
    // 页面骨架的脚手架
    Scaffold(
        //定义头部
        topBar = {
            // 定义顶部栏需要解决两个问题：
            // （1）需要在顶部栏定义顶部的右侧导航菜单
            // （2）需要定义顶部的导航按钮，使得启动侧滑菜单
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
                        if (states.drawerState.currentValue == DrawerValue.Closed) {
                            // 当前为关闭：当用户点击时，打开drawer
                            states.scope.launch {
                                states.drawerState.open()
                            }
                        } else {
                            // 当前为打开：当用户点击时，关闭drawer
                            states.scope.launch {
                                states.drawerState.close()
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
                        states.dropState.value = !states.dropState.value
                    }) {
                        Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "More...")
                        if (states.dropState.value)
                            MenuView(states)
                    }
                })
        },
        //定义底部导航
        bottomBar = {
            BottomView(states)
        },
        //定义悬浮按钮
        floatingActionButton = {
            FloatingActionButton(onClick = {
                states.currentScreen.value = Screen.Home
                states.displayedSnackState.value = !states.displayedSnackState.value
            }) {
                Icon(imageVector = Icons.Filled.Home, contentDescription = "返回")
            }
        },//定义信息提示区
        snackbarHost = {
            if (states.displayedSnackState.value) {
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
            DrawView(states)
        },
    )
}

// 底部导航栏
@Composable
fun BottomView(states: StateHolder) {
    BottomAppBar {
        screens.forEach {
            NavigationBarItem(
                // 选中的按钮被高亮显示
                selected = states.currentScreen.value.route == it.route,
                onClick = {
                    //定义点击动作:切换当前页面
                    states.currentScreen.value = it
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
// 主界面+抽屉界面
fun DrawView(states: StateHolder) {
    ModalNavigationDrawer(
        // 抽屉是否可以通过手势进行交互
        gesturesEnabled = true,
        // 抽屉打开后，遮挡内容的蒙层的颜色
        scrimColor = Color.Gray,
        // 抽屉是否打开
        drawerState = states.drawerState,
        // 抽屉的内容
        drawerContent = {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(360.dp)
                    .background(Color.White)
                    .padding(top = 100.dp)
            ) {
                Row(modifier = Modifier.padding(bottom = 50.dp)) {
                    Image(
                        painter = painterResource(id = R.mipmap.sym_def_app_icon),
                        contentDescription = null
                    )
                    Text(text = "这个家伙很懒，没有留下任何内容...")
                }
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
                        selected = it.route == states.currentScreen.value.route,
                        onClick = {
                            states.scope.launch {
                                states.currentScreen.value = it
                                states.drawerState.close()
                            }
                        })
                }
            }
        },
        content = {
            /**
             * 主屏幕的内容：currentScreen中标记的页面
             * - currentScreen的值改变后，会重新执行这个组合函数，即更新页面
             * - 想要更换屏幕的内容，只需要currentScreen.value=？？？即可（最终都会过来执行对应的loadScreen()函数）
             */
            states.currentScreen.value.loadScreen()
        })

}

// 下拉菜单
@Composable
fun MenuView(states: StateHolder) {
    DropdownMenu(expanded = states.dropState.value,
        onDismissRequest = {
            // 点击其他地方，则关闭下拉框
            states.dropState.value = false
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
                    states.currentScreen.value = it
                    // 点击完之后，关闭下拉框
                    states.dropState.value = false
                })
        }
    }
}

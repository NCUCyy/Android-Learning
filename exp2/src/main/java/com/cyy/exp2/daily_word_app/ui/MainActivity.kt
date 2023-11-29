package com.cyy.exp2.daily_word_app.ui

import android.R
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.cyy.exp2.daily_word_app.PsychologicalTestApp
import com.cyy.exp2.daily_word_app.pojo.Record
import com.cyy.exp2.daily_word_app.pojo.User
import com.cyy.exp2.daily_word_app.view_model.RecordViewModel
import com.cyy.exp2.daily_word_app.view_model.RecordViewModelFactory
import com.cyy.exp2.daily_word_app.view_model.UserViewModel
import com.cyy.exp2.daily_word_app.view_model.UserViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

/**
 *
 * 1、答题界面---用TestViewModel保存界面需要的数据以及题目
 * 2、答题历史界面---RecordViewModel中的records进行展示
 * 3、个人信息界面---需要UserViewModel执行update(user)操作----先更新RecordViewModel中的loginUser，点击确认后，再真正更新数据库中的user
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 默认登录：id为1的用户
        val userId = intent.getIntExtra("userId", 1)
        var record: MutableState<Record?> = mutableStateOf(null)
        // 使用ActivityResultLauncher进行意图跳转
        val resultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            // 意图结束后，执行这个「回调函数」
            ActivityResultCallback {
                if (it.resultCode == RESULT_OK && it.data!!.hasExtra("record")) {
                    // 返回的data数据是个intent类型，里面存储了一段文本内容
                    record.value = it.data!!.getParcelableExtra("record", Record::class.java)!!
                    record.value!!.userId = userId
                    Toast.makeText(this, "完成测试！${record.value.toString()}", Toast.LENGTH_LONG)
                        .show()
                } else {
                    record.value = null
                }
            }
        )
        setContent {
            // 从别的Activity过来的时候，才会执行这里的内容
            MainScreen(userId, resultLauncher, record)
        }
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun NavigationGraphScreen(
    states: StateHolder,
    recordViewModel: RecordViewModel,
    userViewModel: UserViewModel
) {
    // 定义宿主(需要：导航控制器、导航起点---String类型)
    NavHost(navController = states.navController, startDestination = states.startDestination) {
        // 定义有几个页面，就有几个composable(){...}
        // 根据route进行页面的匹配
        // 页面1 Test
        composable(route = Screen.HomePage.route) {
            // 1、页面展示前的数据准备...
            // 2、更新当前显示的Screen
            states.currentScreen.value = Screen.HomePage
            // 3、此语句处才会展示指定的Screen
            HomeScreen(states.resultLauncher, recordViewModel)
        }
        // 页面2 History
        composable(route = Screen.HistoryPage.route) {
            // 1、页面展示前的数据准备...（接收名为”robotStr“参数）
            // 2、更新当前显示的Screen
            states.currentScreen.value = Screen.HistoryPage
            // 3、此语句处才会展示指定的ScreenA
            val records = recordViewModel.records.collectAsStateWithLifecycle()
            HistoryScreen(records)

        }
        // 页面3 User
        composable(route = Screen.UserPage.route) {
            // 1、页面展示前的数据准备（无）
            // 2、更新当前显示的Screen
            states.currentScreen.value = Screen.UserPage
            // 3、此语句处才会展示指定的Screen
            UserScreen(
                recordViewModel.loginUser.collectAsStateWithLifecycle().value!!,
                userViewModel
            )
        }
    }
}

@Composable
fun MenuView(states: StateHolder) {
    val context = LocalContext.current as Activity
    DropdownMenu(expanded = states.dropState.value,
        onDismissRequest = {
            // 点击其他地方，则关闭下拉框
            states.dropState.value = false
        }) {
        DropdownMenuItem(
            // 在前面的Icon
            leadingIcon = {
                Icon(imageVector = Icons.Filled.Star, contentDescription = null)
            },
            text = {
                Text(text = "点赞App", fontSize = 20.sp)
            }, onClick = {
                // 点击完之后，关闭下拉框
                states.dropState.value = false
                Toast.makeText(context, "感谢支持！", Toast.LENGTH_LONG).show()
            })
        DropdownMenuItem(
            // 在前面的Icon
            leadingIcon = {
                Icon(imageVector = Icons.Filled.ExitToApp, contentDescription = null)
            },
            text = {
                Text(text = "退出App", fontSize = 20.sp)
            }, onClick = {
                // 点击完之后，关闭下拉框
                states.dropState.value = false
                ActivityCompat.finishAffinity(context)
                exitProcess(-1)
            })
    }
}


/**
 * 主界面---Scaffold骨架
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    userId: Int,
    resultLauncher: ActivityResultLauncher<Intent>,
    record: MutableState<Record?>
) {
    Log.i("测试一下", record.value.toString())
    // 1、定义状态集合————直接把这个状态集合作为compose组件之间传参的媒介
    // 2、状态定义到顶层————单一数据流
    val states = rememberStates(resultLauncher = resultLauncher)
    // 当前应用的上下文
    val context = LocalContext.current as Activity

    val application = LocalContext.current.applicationContext as PsychologicalTestApp
    val recordViewModel = viewModel<RecordViewModel>(
        factory = RecordViewModelFactory(
            application.recordRepository,
            application.userRepository,
            userId
        )
    )
    val userViewModel = viewModel<UserViewModel>(
        factory = UserViewModelFactory(
            application.userRepository
        )
    )
    var loginUser = recordViewModel.loginUser.collectAsStateWithLifecycle()
    // 添加答题记录
    if (record.value != null) {
        recordViewModel.insert(record.value!!)
        loginUser.value!!.testTurns++
        userViewModel.update(loginUser.value!!)
        record.value = null
        Toast.makeText(context, "添加答题记录成功！", Toast.LENGTH_LONG).show()
    }

    // 脚手架
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (states.drawerState.isClosed)
                    // 抽屉关
                        if (states.currentScreen.value == Screen.HomePage) {
                            // 首页
                            if (loginUser.value != null)
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = states.currentScreen.value.title + loginUser.value!!.username,
                                        textAlign = TextAlign.Center
                                    )
                                }
                        } else
                        // 非首页
                            Text(
                                text = states.currentScreen.value.title,
                                textAlign = TextAlign.Center
                            )
                    else {
                        // 抽屉开
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "菜单",
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                },
                // 左侧图标
                navigationIcon = {
                    // 图标按钮
                    IconButton(onClick = {
                        // 点击按钮，开启异步操作---协程
                        if (states.drawerState.isClosed) {
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
                        if (states.drawerState.isClosed) {
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowRight,
                                contentDescription = "弹出侧滑菜单"
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowLeft,
                                contentDescription = "弹出侧滑菜单"
                            )
                        }

                    }
                },
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
        bottomBar = {
            BottomAppBar {
                screens.forEach {
                    NavigationBarItem(
                        // 是否选中（高亮）
                        selected = it.route == states.currentScreen.value.route,
                        // 点击后更改当前的Screen
                        onClick = {
                            states.scope.launch {
                                states.currentScreen.value = it
                                states.drawerState.close()
                                states.navController.navigate(states.currentScreen.value.route)
                            }
                        },
                        // 标签
                        label = {
                            Text(text = it.label)
                        },
                        // 图标
                        icon = {
                            Icon(painter = painterResource(id = it.icon), contentDescription = null)

                        })
                }
            }
        },
        content = {
            // 页面的主体部分
            Box(modifier = Modifier.padding(it)) {
                // 侧滑导航视图（侧滑界面+导航图）
                DrawView(states, userViewModel, recordViewModel)
            }
        },
        floatingActionButton = {
            // TODO：点击按钮后显示当前的答题情况---showCurRecord来控制
        })
}

/**
 * Drawer（侧滑菜单） + NavGraph（页面主体）
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawView(
    states: StateHolder,
    userViewModel: UserViewModel,
    recordViewModel: RecordViewModel,
) {
    val context = LocalContext.current as Activity
    // TODO：这里需要修改
    val user = recordViewModel.loginUser.collectAsStateWithLifecycle()
    ModalNavigationDrawer(
        // 抽屉是否可以通过手势进行交互
        gesturesEnabled = true,
        // 抽屉打开后，遮挡内容的蒙层的颜色
        scrimColor = Color.Transparent,
        // 抽屉是否打开
        drawerState = states.drawerState,
        // 抽屉的内容
        drawerContent = {
            Card(
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 200.dp),
                shape = RoundedCornerShape(0.dp, 0.dp, 0.dp, 0.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White,
                ),
                modifier = Modifier
                    .fillMaxHeight()
                    .width(260.dp)
            ) {
                Row(modifier = Modifier.padding(top = 50.dp, start = 10.dp, bottom = 30.dp)) {
                    Image(
                        painter = painterResource(id = R.mipmap.sym_def_app_icon),
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            states.scope.launch {
                                states.drawerState.close()
                            }
                            states.navController.navigate(Screen.UserPage.route)
                        }
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    if (user.value != null)
                        Text(text = user.value!!.username, fontSize = 30.sp)
                }
                Card(
                    modifier = Modifier.padding(10.dp),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 200.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent,
                    ),
                ) {
                    // 抽屉中要显示的内容
                    screens.forEachIndexed { index, it ->
                        NavigationDrawerItem(
                            shape = RoundedCornerShape(0.dp, 0.dp, 0.dp, 0.dp),
                            label = {
                                Text(it.label, fontSize = 20.sp)
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(id = it.icon),
                                    contentDescription = it.title
                                )
                            },
                            // 选中的按钮被高亮显示
                            selected = it.route == states.currentScreen.value.route,
                            onClick = {
                                states.scope.launch {
                                    states.currentScreen.value = it
                                    states.navController.navigate(states.currentScreen.value.route)
                                    states.drawerState.close()
                                }
                            })
                        if (index != screens.size - 1)
                            Divider(thickness = 2.dp)
                    }
                }
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp, top = 220.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF10656),
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 10.dp),
                    onClick = {
                        context.finish()
                    }) {
                    Text(text = "退出登录", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
            }
        },
        content = {
            // 主体是导航图
            NavigationGraphScreen(states, recordViewModel, userViewModel)
        })
}

/**
 * 状态集合（对状态的统一管理）
 */
@OptIn(ExperimentalMaterial3Api::class)
class StateHolder(
    // 当前页面是谁（只用于：bottomBar的selected中底部导航栏高亮显示当前页面的选项）
    val currentScreen: MutableState<Screen>,
    // 导航控制器：宿主、点击动作都需要用到
    val navController: NavHostController,
    // 导航起点---route: String
    val startDestination: String,
    // 用于打开Drawer
    val scope: CoroutineScope,
    // 用于判断Drawer 是否打开
    val drawerState: DrawerState,
    val dropState: MutableState<Boolean>,
    val resultLauncher: ActivityResultLauncher<Intent>
)

/**
 * 返回StateHolder类型（需要添加@Composable注解，才能使用remember{...}，但是本身并不是一个真正用于显示的可组合函数）
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberStates(
    currentScreen: MutableState<Screen> = remember { mutableStateOf(Screen.HomePage) },
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.HomePage.route,
    scope: CoroutineScope = rememberCoroutineScope(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    dropState: MutableState<Boolean> = mutableStateOf(false),
    resultLauncher: ActivityResultLauncher<Intent>
) = StateHolder(
    currentScreen,
    navController,
    startDestination,
    scope,
    drawerState,
    dropState,
    resultLauncher
)

@Composable
fun MainScreen_Demo(userId: Int) {
    val context = LocalContext.current
    val application = LocalContext.current.applicationContext as PsychologicalTestApp
    val recordViewModel = viewModel<RecordViewModel>(
        factory = RecordViewModelFactory(
            application.recordRepository,
            application.userRepository,
            userId
        )
    )
    val records = recordViewModel.records.collectAsStateWithLifecycle()
    val loginUser = recordViewModel.loginUser.collectAsStateWithLifecycle()
    // 测试跳转是否成功------------已成功
    demo(recordViewModel, loginUser, records, userId)
}

@Composable
fun demo(
    recordViewModel: RecordViewModel,
    loginUser: State<User?>,
    records: State<List<Record>>,
    userId: Int
) {
    Column {
        Text(text = "欢迎回来，${loginUser.value}!")
        Button(onClick = {
//            recordViewModel.insert(Record(testTime = OffsetDateTime.now(), category = "", "category", userId))
        }) {
            Text(text = "添加Record")
        }
        LazyColumn {
            items(records.value) {
                Row {
                    Text(text = it.toString())
                }
            }
        }
    }
}
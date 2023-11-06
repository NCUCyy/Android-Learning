package com.cyy.exp2.memo

import android.R
import android.R.attr.fragment
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusRequester.Companion.createRefs
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import kotlin.system.exitProcess


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /**
         * 获得当前Activity的ViewModel---方法一
         */
        // 创建一个 ViewModelProvider 实例
        val viewModelProvider = ViewModelProvider(this)
        // 获取指定类型的 ViewModel
        val viewModel = viewModelProvider[MemoViewModel::class.java]
        setContent {
            MainScreen()
        }
    }
}


/**
 * 技术：
 * 1、ConstraintLayout受限布局
 * 2、Image的Modifier.clickable{...}
 */
@SuppressLint("RememberReturnType")
@Composable
fun MemoCard(memo: Memo, navController: NavHostController) {
    // 创建一个 ViewModelProvider 实例
    val viewModelProvider = ViewModelProvider(LocalContext.current as ViewModelStoreOwner)

    // 获取指定类型的 ViewModel
    val memoViewModel = viewModelProvider[MemoViewModel::class.java]
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .clickable {
                // 两个任务：1、传递被点击的Robot数据；2、导航到详情界面
                memoViewModel.setCur(memo)
                Log.i("MyLog2", memoViewModel.cur.value.toString())
                // 处理图标的点击动作（导航到指robot的详情页面）
                navController.navigate("${Screen.MemoDetailPage.route}") {
                    popUpTo(Screen.MemoListPage.route)
                    launchSingleTop = true
                }
            },
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(255, 204, 0),
            contentColor = Color.Black
        )
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
            var (contentRef, timeRef) = remember {
                createRefs()
            }
            val vGuideline = createGuidelineFromStart(0.45f)
            val hGuideline = createGuidelineFromTop(0.7f)
            Text(
                text = memo.content,
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 10.dp)
                    .constrainAs(contentRef) {
                        top.linkTo(parent.top)
                        bottom.linkTo(hGuideline)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                fontSize = 20.sp
            )
            // 转化时间表示方式，用于显示
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val modifyTime = memo.modifyTime.format(formatter)
            Text(
                text = "上次修改：$modifyTime",
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .constrainAs(timeRef) {
                        top.linkTo(hGuideline)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(vGuideline)
                        end.linkTo(parent.end)
                    },
                fontSize = 13.sp,
                color = Color.DarkGray
            )
        }
    }
}

// 1、列表界面
@Composable
fun MemoListScreen(states: StateHolder) {
    // 创建一个 ViewModelProvider 实例
    val viewModelProvider = ViewModelProvider(LocalContext.current as ViewModelStoreOwner)

    // 获取指定类型的 ViewModel
    val memoViewModel = viewModelProvider[MemoViewModel::class.java]
    val memos = memoViewModel.memos.collectAsState()
    LazyColumn {
        items(memos.value) { it: Memo ->
            MemoCard(memo = it, states.navController)
        }
    }
}

// 2、详情界面（点击进入）
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoDetailScreen(isNew: Boolean = false) {
    // 创建一个 ViewModelProvider 实例
    val viewModelProvider = ViewModelProvider(LocalContext.current as ViewModelStoreOwner)

    // 获取指定类型的 ViewModel
    val memoViewModel = viewModelProvider[MemoViewModel::class.java]
    var input = memoViewModel.input.collectAsState()
    if (!isNew) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            TextField(modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
                value = input.value,
                onValueChange = { it: String ->
                    memoViewModel.changeInput(it)
                })
        }
    } else {
        // 内部自动设置了当前的cur是新添加的memo
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            TextField(modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
                value = input.value,
                onValueChange = { it: String ->
                    memoViewModel.changeInput(it)
                })
        }
    }

}

// 3、用户界面
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun UserScreen() {
    // 创建一个 ViewModelProvider 实例
    val viewModelProvider = ViewModelProvider(LocalContext.current as ViewModelStoreOwner)
    // 获取指定类型的 ViewModel
    val userViewModel = viewModelProvider[UserViewModel::class.java]
    val user = userViewModel.user.collectAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.mipmap.sym_def_app_icon),
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
        )
        Text(text = "用户名：" + user.value!!.username, fontSize = 20.sp)
        Text(text = "出生日期：" + user.value!!.birth.toString(), fontSize = 20.sp)
        Text(text = "性别：" + user.value!!.gender, fontSize = 20.sp)
    }
}


/**
 * 技术：
 * 1、NavController
 * 2、NavHost、composable
 * 3、导航跳转时，参数的接收（宿主）与发送（源）
 * 4、作为Scaffold的content展示
 * 5、route的理解（String）
 *
 * 导航---Host
 *
 * 思考：原来的做法（即Scaffold-Model中的写法）：每个Screen都包含一个loadScreen()函数（该函数返回一个组合函数），通过调用这个函数，就可以直接把预先定义好的Screen加载出来
 */
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun NavigationGraphScreen(states: StateHolder) {
    // 创建一个 ViewModelProvider 实例
    val viewModelProvider = ViewModelProvider(LocalContext.current as ViewModelStoreOwner)

    // 获取指定类型的 ViewModel
    val memoViewModel = viewModelProvider[MemoViewModel::class.java]
    // 定义宿主(需要：导航控制器、导航起点---String类型)
    NavHost(navController = states.navController, startDestination = states.startDestination) {
        // 定义有几个页面，就有几个composable(){...}
        // 根据route进行页面的匹配
        // 页面1
        composable(route = Screen.MemoListPage.route) {
            // 1、页面展示前的数据准备...
            // 2、更新当前显示的Screen
            states.currentScreen.value = Screen.MemoListPage
            // 3、此语句处才会展示指定的Screen
            MemoListScreen(states)
        }
        // 页面2.1：modify memo
        composable(route = Screen.MemoDetailPage.route) {
            // 1、页面展示前的数据准备...
            // 更新当前查看的机器人是谁？
            val cur = memoViewModel.cur.collectAsState()
            states.memoState.value = cur.value
            Log.i("MyLog3", memoViewModel.cur.value.toString())

            // 2、更新当前显示的Screen
            states.currentScreen.value = Screen.MemoDetailPage
            Log.i("MyLog", cur.value.toString())

            // 3、此语句处才会展示指定的Screen
            MemoDetailScreen()
        }
        // 页面2.2：new memo
        composable(route = Screen.MemoDetailPage.route + "/new") {
            // 1、页面展示前的数据准备...（接收名为”robotStr“参数）
            // 2、更新当前显示的Screen
            states.currentScreen.value = Screen.MemoDetailPage
            // 3、此语句处才会展示指定的Screen
            MemoDetailScreen(isNew = true)
        }
        // 页面3
        composable(route = Screen.UserPage.route) {
            // 1、页面展示前的数据准备（无）
            // 2、更新当前显示的Screen
            states.currentScreen.value = Screen.UserPage
            // 3、此语句处才会展示指定的Screen
            UserScreen()
        }
    }
}

val screens = listOf(Screen.MemoListPage, Screen.MemoDetailPage, Screen.UserPage)

/**
 *Screen类（与用于显示的Screen实体不同！要区分开！Screen类只用于提供页面需要的元数据metaData：icon、title、"route"【用于导航】）
 */
sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object MemoListPage :
        Screen(route = "memoList", title = "备忘录列表", icon = Icons.Filled.List)

    object MemoDetailPage :
        Screen(route = "memoDetail", title = "备忘录详情", icon = Icons.Filled.Edit)

    object UserPage :
        Screen(route = "userInfo", title = "用户信息", icon = Icons.Filled.AccountCircle)
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
                finishAffinity(context)
                exitProcess(-1)
            })
    }
}


/**
 * 主界面---Scaffold骨架
 */
@Preview
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    // 1、定义状态集合————直接把这个状态集合作为compose组件之间传参的媒介
    // 2、状态定义到顶层————单一数据流
    val states = rememberStates()
    // 当前应用的上下文
    val context = LocalContext.current as Activity
    // 创建一个 ViewModelProvider 实例
    val viewModelProvider = ViewModelProvider(LocalContext.current as ViewModelStoreOwner)

    // 获取指定类型的 ViewModel
    val memoViewModel = viewModelProvider[MemoViewModel::class.java]
    // 脚手架
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (states.drawerState.isClosed)
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = states.currentScreen.value.title,
                                textAlign = TextAlign.Center
                            )
                        }
                    else {
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
                            if (it.route == Screen.MemoDetailPage.route) {
                                // 若要去”详情页面“，需要单独判断（robotState为null的情况）
                                if (states.memoState.value == null) {
                                    Toast.makeText(
                                        context,
                                        "请选择要查看的备忘录！",
                                        Toast.LENGTH_LONG
                                    )
                                        .show()
                                } else {
                                    // 到详情页面，一定需要robotStr参数（所以需要单独出来写）
                                    states.navController.navigate("${it.route}") {
                                        // 回退操作（采用直接回退到RobotListPage页面）
                                        popUpTo(Screen.MemoListPage.route)
                                        launchSingleTop = true
                                    }
                                }
                            } else {
                                // 若去其他页面
                                // 实现导航---获取导航控制器、根据页面的route进行匹配（String类型！！！）
                                // 相当于进”导航栈“
                                states.navController.navigate(it.route) {
                                    // 回退操作（采用直接回退到RobotListPage页面）
                                    popUpTo(Screen.MemoListPage.route)
                                    launchSingleTop = true
                                }
                            }
                        },
                        // 标签
                        label = {
                            Text(text = it.title)
                        },
                        // 图标
                        icon = {
                            Icon(imageVector = it.icon, contentDescription = null)
                        })
                }
            }
        },
        content = {
            // 页面的主体部分
            Box(modifier = Modifier.padding(it)) {
                // 侧滑导航视图（侧滑界面+导航图）
                DrawView(states)
            }
        },
        floatingActionButton = {
            if (states.currentScreen.value == Screen.MemoListPage) {
                FloatingActionButton(onClick = {
                    // 跳转到new Memo界面
                    // 一点击按钮就需要创建
                    memoViewModel.add()
                    states.navController.navigate("${Screen.MemoDetailPage.route}/new")
                }, shape = RoundedCornerShape(100.dp)) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "返回",
                        Modifier.size(25.dp)
                    )
                }
            } else if (states.currentScreen.value == Screen.MemoDetailPage) {
                FloatingActionButton(onClick = {
                    // 跳转到详情界面
                    states.navController.navigate(Screen.MemoListPage.route)
                    Toast.makeText(context, "保存成功！！", Toast.LENGTH_LONG).show()
                }, shape = RoundedCornerShape(100.dp)) {
                    Icon(
                        imageVector = Icons.Filled.Done,
                        contentDescription = "完成",
                        Modifier.size(25.dp)
                    )
                }
            } else if (states.currentScreen.value == Screen.UserPage) {
                FloatingActionButton(onClick = {
                    // 跳转到详情界面
                    states.navController.navigate(Screen.MemoListPage.route)
                }, shape = RoundedCornerShape(100.dp)) {
                    Icon(
                        imageVector = Icons.Filled.Home,
                        contentDescription = "完成",
                        Modifier.size(25.dp)
                    )
                }

            }
        })
}

/**
 * Drawer（侧滑菜单） + NavGraph（页面主体）
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawView(states: StateHolder) {
    // 创建一个 ViewModelProvider 实例
    val viewModelProvider = ViewModelProvider(LocalContext.current as ViewModelStoreOwner)

    // 获取指定类型的 ViewModel
    val userViewModel = viewModelProvider[UserViewModel::class.java]
    val user = userViewModel.user.collectAsState()
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
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            states.scope.launch {
                                states.drawerState.close()
                            }
                            states.navController.navigate(Screen.UserPage.route)
                        }
                    )
                    Text(text = user.value!!.username, fontSize = 30.sp)
                }
                // 抽屉中要显示的内容
                screens.forEach {
                    NavigationDrawerItem(
                        label = {
                            Text(it.title, fontSize = 20.sp)
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
                                states.navController.navigate(states.currentScreen.value.route)
                                states.drawerState.close()
                            }
                        })
                }
            }
        },
        content = {
            // 主体是导航图
            NavigationGraphScreen(states)
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
    // 当前查看的robot是谁
    val memoState: MutableState<Memo?>,
    // 用于打开Drawer
    val scope: CoroutineScope,
    // 用于判断Drawer 是否打开
    val drawerState: DrawerState,
    val dropState: MutableState<Boolean>
)

/**
 * 返回StateHolder类型（需要添加@Composable注解，才能使用remember{...}，但是本身并不是一个真正用于显示的可组合函数）
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberStates(
    currentScreen: MutableState<Screen> = remember { mutableStateOf(Screen.MemoListPage) },
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.MemoListPage.route,
    memoState: MutableState<Memo?> = remember { mutableStateOf(null) },
    scope: CoroutineScope = rememberCoroutineScope(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    dropState: MutableState<Boolean> = mutableStateOf(false)
) = StateHolder(
    currentScreen,
    navController,
    startDestination,
    memoState,
    scope,
    drawerState,
    dropState
)

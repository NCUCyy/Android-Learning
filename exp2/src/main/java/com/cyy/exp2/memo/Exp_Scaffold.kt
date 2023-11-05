package com.cyy.exp2.memo

import android.R
import android.annotation.SuppressLint
import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Memo(val content: String, val modifyTime: LocalDateTime, val createTime: LocalDateTime)

/**
 * 技术：
 * 1、ConstraintLayout受限布局
 * 2、Image的Modifier.clickable{...}
 */
@SuppressLint("RememberReturnType")
@Composable
fun MemoCard(memo: Memo, navController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .clickable {
                // 两个任务：1、传递被点击的Robot数据；2、导航到详情界面
                // 转化为String再传（因为只能接收【基元类型】）
                val memoStr = Gson().toJson(memo)
                // 处理图标的点击动作（导航到指robot的详情页面）
                navController.navigate("${Screen.RobotPage.route}/${memoStr}") {
                    popUpTo(Screen.MemoListScreen.route)
                    launchSingleTop = true
                }
            },
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Blue, contentColor = Color.Yellow)
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
            var (contentRef, timeRef) = remember {
                createRefs()
            }
//            val vGuideline = createGuidelineFromStart(0.4f)
            val hGuideline = createGuidelineFromTop(0.7f)
            Text(text = memo.content, modifier = Modifier.constrainAs(contentRef) {
                top.linkTo(parent.top)
                bottom.linkTo(hGuideline)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }, fontSize = 20.sp)
            // 转化时间表示方式，用于显示
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val modifyTime = memo.modifyTime.format(formatter)
            Text(text = modifyTime, modifier = Modifier.constrainAs(timeRef) {
                top.linkTo(hGuideline)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }, fontSize = 18.sp)
        }
    }
}

// 1、列表界面
@Composable
fun MemoListScreen(states: StateHolder, memos: MutableList<Memo>) {
    LazyColumn {
        items(memos) { it: Memo ->
            MemoCard(memo = it, states.navController)
        }
    }
}

// 2、详情界面（点击进入）
@Composable
fun MemoDetailScreen(memo: Memo) {
    Box(contentAlignment = Alignment.Center) {
        Column {
            Text(text = "-----这是${memo.content}的详情页面-----", color = Color.Blue)
        }
    }
}

// 3、关于界面
@Composable
fun UserScreen() {
    Box(contentAlignment = Alignment.Center) {
        Text(text = "用户信息界面")
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
@Composable
fun NavigationGraphScreen(states: StateHolder) {
    // 定义宿主(需要：导航控制器、导航起点---String类型)
    NavHost(navController = states.navController, startDestination = states.startDestination) {
        // 定义有几个页面，就有几个composable(){...}
        // 根据route进行页面的匹配
        // 页面1
        composable(route = Screen.RobotListPage.route) {
            // 1、页面展示前的数据准备...
            val robots = mutableListOf<Robot>()
            for (i in 0..10) {
                robots.add(Robot("机器人${i}", "机器人${i}", R.mipmap.sym_def_app_icon))
            }
            // 2、更新当前显示的Screen
            states.currentScreen.value = Screen.RobotListPage
            // 3、此语句处才会展示指定的Screen
            MemoListScreen(states, memos)
        }
        // 页面2
        composable(route = Screen.MemoDetailPage.route + "/{memoStr}", arguments = listOf(
            navArgument("memoStr") {
                type = NavType.StringType
            }
        )) {
            // 1、页面展示前的数据准备...（接收名为”robotStr“参数）
            // （从①RobotCard跳转过来；或②直接点击底部导航栏过来————都需要传递一个robotStr参数，来指定当前页面需要展示哪个robot的数据）
            val memoStr: String = it.arguments?.getString("memoStr")!!
            val memo: Memo = Gson().fromJson(memoStr, Memo::class.java)
            // 更新当前查看的机器人是谁？
            states.memoState.value = memo
            // 2、更新当前显示的Screen
            states.currentScreen.value = Screen.MemoDetailPage
            // 3、此语句处才会展示指定的Screen
            MemoDetailScreen(memo)
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
            )
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
                                    Toast.makeText(context, "未选择Memo！", Toast.LENGTH_LONG)
                                        .show()
                                } else {
                                    val robotStr = Gson().toJson(states.robotState.value)
                                    // 到详情页面，一定需要robotStr参数（所以需要单独出来写）
                                    states.navController.navigate("${it.route}/${robotStr}") {
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
            FloatingActionButton(onClick = {
                // 回退
                states.navController.popBackStack()
            }) {
                Icon(imageVector = Icons.Filled.KeyboardArrowLeft, contentDescription = "返回")
            }
        })
}

/**
 * Drawer（侧滑菜单） + NavGraph（页面主体）
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
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
) = StateHolder(currentScreen, navController, startDestination, memoState, scope, drawerState)

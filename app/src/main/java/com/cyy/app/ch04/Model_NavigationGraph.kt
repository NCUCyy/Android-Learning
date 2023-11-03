package com.cyy.app.ch04

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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

data class Robot(val name: String, val description: String, val icon: Int)

@Composable
fun RobotCard(robot: Robot, navController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Blue, contentColor = Color.Yellow)
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
            var (nameRef, descRef, iconRef) = remember {
                createRefs()
            }
            val vGuideline = createGuidelineFromStart(0.4f)
            val hGuideline = createGuidelineFromTop(0.4f)
            Image(
                modifier = Modifier
                    .constrainAs(iconRef) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(vGuideline)
                    }
                    .clickable {
                        // 两个任务：1、传递被点击的Robot数据；2、导航到详情界面
                        // 转化为String再传（因为只能接收【基元类型】）
                        val robotStr = Gson().toJson(robot)
                        // 处理图标的点击动作（导航到指robot的详情页面）
                        navController.navigate("${Screen.RobotPage.route}/${robotStr}")
                    },
                painter = painterResource(id = robot.icon),
                contentDescription = null
            )
            Text(text = robot.name, modifier = Modifier.constrainAs(nameRef) {
                top.linkTo(parent.top)
                bottom.linkTo(hGuideline)
                start.linkTo(vGuideline)
                end.linkTo(parent.end)
            }, fontSize = 20.sp)
            Text(text = robot.description, modifier = Modifier.constrainAs(descRef) {
                top.linkTo(hGuideline)
                bottom.linkTo(parent.bottom)
                start.linkTo(vGuideline)
                end.linkTo(parent.end)
            }, fontSize = 18.sp)
        }
    }
}

// 1、列表界面
@Composable
fun RobotListScreen(nacController: NavHostController, robots: MutableList<Robot>) {
    LazyColumn {
        items(robots) { it: Robot ->
            RobotCard(robot = it, nacController)
        }
    }
}

// 2、详情界面（点击进入）
@Composable
fun RobotDetailScreen(robot: Robot) {
//    val robot: Robot = Robot("机器人-测试", "机器人-测试", android.R.mipmap.sym_def_app_icon)
    Box(contentAlignment = Alignment.Center) {
        Column {
            Text(text = "-----这是${robot.name}的详情页面-----", color = Color.Blue)
            Image(painter = painterResource(id = robot.icon), contentDescription = null)
            Text(text = robot.description, color = Color.Green)
        }
    }
}

// 3、关于界面
@Composable
fun AboutScreen() {
    Box(contentAlignment = Alignment.Center) {
        Text(text = "About界面")
    }
}

/**
 * 导航---Host
 *
 * 思考：原来的做法（即Scaffold-Model中的写法）：每个Screen都包含一个loadScreen()函数（该函数返回一个组合函数），通过调用这个函数，就可以直接把预先定义好的Screen加载出来
 */
@Composable
fun NavigationGraphScreen(
    navController: NavHostController,
    startDestination: String,
    robotState: MutableState<Robot?>,
    currentScreen: MutableState<Screen>
) {
    // 定义宿主(需要：导航控制器、导航起点---String类型)
    NavHost(navController = navController, startDestination = startDestination) {
        // 定义有几个页面，就有几个composable(){...}
        // 根据route进行页面的匹配
        // 页面1
        composable(route = Screen.RobotListPage.route) {
            // 1、页面展示前的数据准备...
            val robots = mutableListOf<Robot>()
            for (i in 0..10) {
                robots.add(Robot("机器人${i}", "机器人${i}", android.R.mipmap.sym_def_app_icon))
            }
            // 2、更新当前显示的Screen
            currentScreen.value = Screen.RobotListPage
            // 3、此语句处才会展示指定的Screen
            RobotListScreen(navController, robots)
        }
        // 页面2
        composable(route = Screen.RobotPage.route + "/{robotStr}", arguments = listOf(
            navArgument("robotStr") {
                type = NavType.StringType
            }
        )) {
            // 1、页面展示前的数据准备...（接收名为”robotStr“参数）
            // （从①RobotCard跳转过来；或②直接点击底部导航栏过来————都需要传递一个robotStr参数，来指定当前页面需要展示哪个robot的数据）
            val robotStr: String = it.arguments?.getString("robotStr")!!
            val robot: Robot = Gson().fromJson(robotStr, Robot::class.java)
            // 更新当前查看的机器人是谁？（）
            robotState.value = robot
            // 2、更新当前显示的Screen
            currentScreen.value = Screen.RobotPage
            // 3、此语句处才会展示指定的Screen
            RobotDetailScreen(robot)
        }
        // 页面3
        composable(route = Screen.AboutPage.route) {
            // 1、页面展示前的数据准备（无）
            // 2、更新当前显示的Screen
            currentScreen.value = Screen.AboutPage
            // 3、此语句处才会展示指定的Screen
            AboutScreen()
        }
    }
}

val screens = listOf(Screen.RobotListPage, Screen.RobotPage, Screen.AboutPage)

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object RobotListPage :
        Screen(route = "robotList", title = "机器人列表", icon = Icons.Filled.List)

    object RobotPage : Screen(route = "robot", title = "机器人详情", icon = Icons.Filled.Face)
    object AboutPage : Screen(route = "about", title = "关于应用", icon = Icons.Filled.Info)
}

@Preview
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    // 所有数据放到顶层
    // 当前页面是谁（只用于：bottomBar的selected中底部导航栏高亮显示当前页面的选项）
    val currentScreen = remember {
        mutableStateOf<Screen>(Screen.RobotListPage)
    }
    // 导航控制器：宿主、点击动作都需要用到
    val navController: NavHostController = rememberNavController()
    // 导航起点---route: String
    val startDestination = Screen.RobotListPage.route
    // 当前查看的robot是谁
    val robotState = remember {
        mutableStateOf<Robot?>(null)
    }
    val context = LocalContext.current as Activity
    // 脚手架
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = currentScreen.value.title)
                },
                navigationIcon = {
                    Icon(imageVector = currentScreen.value.icon, contentDescription = null)
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                screens.forEach {
                    NavigationBarItem(
                        // 是否选中（高亮）
                        selected = it.route == currentScreen.value.route,
                        // 点击后更改当前的Screen
                        onClick = {
                            // 只有点击不同的导航选项，才需要跳转（减少页面的无效闪烁）
                            if (currentScreen.value.route != it.route) {
                                if (it.route == Screen.RobotPage.route) {
                                    // 若要去”详情页面“，需要单独判断（robotState为null的情况）
                                    if (robotState.value == null) {
                                        Toast.makeText(context, "未选择Robot！", Toast.LENGTH_LONG)
                                            .show()
                                    } else {
//                                        currentScreen.value = it
                                        val robotStr = Gson().toJson(robotState.value)
                                        // 到详情页面，一定需要robotStr参数（所以需要单独出来写）
                                        navController.navigate("${it.route}/${robotStr}") {
                                            // 回退操作（采用直接回退到RobotListPage页面）
                                            popUpTo(Screen.RobotListPage.route)
                                        }
                                    }
                                } else {
                                    // 若去其他页面
                                    // 更新全局变量---当前页面是哪个？
//                                    currentScreen.value = it
                                    // 实现导航---获取导航控制器、根据页面的route进行匹配（String类型！！！）
                                    // 相当于进”导航栈“
                                    navController.navigate(it.route) {
                                        // 回退操作（采用直接回退到RobotListPage页面）
                                        popUpTo(Screen.RobotListPage.route)
                                    }
                                    /**
                                     *回退操作有三种类型可以选择：
                                     * 1、popUpTo("about")：导航栈出栈到栈顶为"about"为止（理解：点击回退后，把当前Screen到about的所有记录都删掉{about还留着，也就是跳到about界面}）
                                     * 2、popUpTo("about"){ inclusive=true }：导航栈出栈到栈顶为"about"后，把about也出栈
                                     * 3、navigate(route){ launchSingleTop = true }；当前要去的route不是导航栈的顶部，则导航过去；否则不去
                                     *
                                     */
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
                NavigationGraphScreen(navController, startDestination, robotState, currentScreen)
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                // 回退
                navController.popBackStack()
            }) {
                Icon(imageVector = Icons.Filled.KeyboardArrowLeft, contentDescription = "返回")
            }
        })
}
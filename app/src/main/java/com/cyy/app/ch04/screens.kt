package com.cyy.app.ch04

import android.annotation.SuppressLint
import android.icu.text.CaseMap.Title
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import java.nio.channels.AlreadyBoundException

data class Robot(val name: String, val description: String, val icon: Int)

@Composable
fun RobotCard(robot: Robot) {
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
                        // 处理图标的点击动作（导航到指定的详情页面）

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
@Preview
@Composable
fun RobotListScreen() {
    val robots = mutableListOf<Robot>()
    for (i in 0..10) {
        robots.add(Robot("机器人${i}", "机器人${i}", android.R.mipmap.sym_def_app_icon))
    }
    LazyColumn {
        items(robots) { it: Robot ->
            RobotCard(robot = it)
        }
    }
}

// 2、详情界面（点击进入）
@Composable
fun RobotDetailScreen() {
    val robot: Robot = Robot("机器人-测试", "机器人-测试", android.R.mipmap.sym_def_app_icon)
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
fun NavigationGraphScreen(navController: NavHostController, startDestination: String) {
    NavHost(navController = navController, startDestination = Screen.RobotListPage.route) {
        // 根据route进行页面的匹配
        composable(route = Screen.RobotListPage.route) {
            RobotListScreen()
        }
        composable(route = Screen.RobotPage.route) {
            RobotDetailScreen()
        }
        composable(route = Screen.AboutPage.route) {
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
    val currentScreen = remember {
        mutableStateOf<Screen>(Screen.RobotListPage)
    }
    val navController: NavHostController = rememberNavController()
    val startDestination = Screen.RobotListPage.route
    Scaffold(
        topBar = {

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
                                // 更新全局变量---当前页面是哪个？
                                currentScreen.value = it
                                // 真正的跳转操作---根据页面的route进行匹配
                                navController.navigate(currentScreen.value.route)
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
            Box(modifier = Modifier.padding(it)) {
                NavigationGraphScreen(navController, startDestination)
            }
        },
        floatingActionButton = {

        })
}
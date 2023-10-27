package com.cyy.app.ch03

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.cyy.app.R

/**
 * 1、Sealed class（密封类） 是一个有特定数量子类的类，看上去和枚举有点类似
 * - 区别：在枚举中，我们每个类型只有一个对象（实例）；而在密封类中，同一个类可以拥有几个对象。
 *
 * 2、Sealed class（密封类）的所有子类都必须与密封类在同一文件中
 *
 * 3、Sealed class（密封类）的子类的子类可以定义在任何地方，并不需要和密封类定义在同一个文件中
 *
 * 4、Sealed class（密封类）没有构造函数，不可以直接实例化，只能实例化内部的子类
 */
/**
 * 定义要切换界面的密封类Screen
 * - 为了方便后续对这三个界面的切换，定义一个通用的密封类Screen
 * @property route String 导航线路名
 * @property title String  标题
 * @property icon ImageVector 图标
 * @property loadScreen [@androidx.compose.runtime.Composable] Function0<Unit> 加载动作处理
 * @constructor
 */
sealed class Screen(
    // 路由：用于确定当前显示的是哪个页面
    val route: String,
    // 显示的文本
    val title: String,
    // Icon图标
    val icon: ImageVector,
    // 返回值是一个组合函数
    val loadScreen: @Composable () -> Unit
) {
    object Home : Screen("home", "首页", Icons.Filled.Home, loadScreen = {
        HomeScreen()
    })

    object Setting : Screen("setting", "配置", Icons.Filled.Settings, loadScreen = {
        SettingScreen()
    })

    object Help : Screen("help", "帮助和支持", Icons.Filled.Info, loadScreen = {
        HelpScreen()
    })
}

@Composable
fun DisplayScreen(
    title: String,
    preColor: Color = Color.Black,
    backgroundColor: Color = colorResource(R.color.teal_200)
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Text(text = title, fontSize = 30.sp, color = preColor)
    }
}

@Composable
fun HomeScreen() {
    DisplayScreen(title = "首页")
}

@Composable
fun SettingScreen() {
    DisplayScreen(title = "配置")
}

@Composable
fun HelpScreen() {
    DisplayScreen(title = "帮助和支持")
}

@Preview
@Composable
fun Test() {
    // 使用
    Screen.Home.loadScreen()
}
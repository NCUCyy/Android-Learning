package com.cyy.transapp.activity.main


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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBarItem
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.cyy.transapp.R
import com.cyy.transapp.TransApp
import com.cyy.transapp.activity.main.view.DrawerView
import com.cyy.transapp.activity.main.view.MenuView
import com.cyy.transapp.view_model.trans.QueryViewModel
import com.cyy.transapp.view_model.trans.QueryViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO：登录成功后，只是传过来了一个userId！
        // 默认给个id = 1，用于测试
        val userId = intent.getIntExtra("userId", 22)

        // TODO：注意要定义为MutableState！
        val vocabulary = mutableStateOf("")

        // 使用ActivityResultLauncher进行意图跳转
        val resultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            // 意图结束后，执行这个「回调函数」
            ActivityResultCallback {
                if (it.resultCode == RESULT_OK) {
                    if (it.data!!.hasExtra("vocabulary")) {
                        // 返回的data数据是个intent类型，里面存储了一段文本内容
                        vocabulary.value =
                            it.data?.getStringExtra("vocabulary")!!
                        // vocabulary更新后，会重组整个UI界面
                    }
                    Toast.makeText(this, "回到MainActivity", Toast.LENGTH_LONG).show()
                }
            }
        )
        setContent {
            Log.i("MainActivity---Look", vocabulary.toString())
            MainScreen(userId, resultLauncher, vocabulary.value)
        }
    }
}


/**
 * 主界面---Scaffold骨架
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    userId: Int,
    resultLauncher: ActivityResultLauncher<Intent>,
    vocabulary: String
) {
    val states = rememberStates(resultLauncher)
    val context = LocalContext.current as Activity
    val application = context.application as TransApp

    // TODO：查词记录的ViewModel
    val queryViewModel =
        viewModel<QueryViewModel>(
            factory = QueryViewModelFactory(
                userId,
                application.transRepository,
                application.sentenceRepository
            )
        )

    // 删除翻译记录的对话框
    if (states.showDeleteDialog.value) {
        DeleteDialog(states, queryViewModel::clearAllTransRecords)
    }
    // 脚手架
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = states.currentScreen.value.title,
                            textAlign = TextAlign.Center
                        )
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
                        Icon(
                            painter = painterResource(id = R.drawable.user),
                            contentDescription = null,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                },
                actions = {
                    if (states.currentScreen.value.route == Screen.QueryPage.route) {
                        IconButton(onClick = {
                            states.showDeleteDialog.value = true
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.delete_history),
                                contentDescription = null,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    } else if (states.currentScreen.value.route == Screen.ListenPage.route) {
                        IconButton(onClick = {
                            states.dropState.value = !states.dropState.value
                        }) {
                            Icon(imageVector = Icons.Filled.MoreVert, contentDescription = null)
                            if (states.dropState.value)
                                MenuView(states)
                        }
                    }
                }
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
                            states.currentScreen.value = it
                            states.navController.navigate(it.route)
                        },
                        // 标签
                        label = {
                            Text(text = it.title)
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
                DrawerView(states, userId, vocabulary)
            }
        },
        floatingActionButton = {
        })
}

@Composable
fun DeleteDialog(states: StateHolder, action: () -> Unit) {
    Dialog(onDismissRequest = {
        states.showDeleteDialog.value = false
    }) {
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White,
                ),
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Text(
                    text = "确定是否清空？",
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 15.dp, bottom = 5.dp)
                )
                Text(
                    text = "清空后将无法恢复哦~",
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    color = Color.Gray
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    DialogButton(
                        color = Color(0xFFFA85AD),
                        text = "取消",
                        icon = R.drawable.cancel
                    ) {
                        states.showDeleteDialog.value = false
                    }
                    Spacer(
                        modifier = Modifier.width(20.dp)
                    )
                    DialogButton(
                        color = Color(0xFF88D4F7),
                        text = "确认",
                        icon = R.drawable.check
                    ) {
                        action.invoke()
                        states.showDeleteDialog.value = false
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
fun DialogButton(color: Color, text: String, icon: Int, action: () -> Unit) {
    Button(
        onClick = {
            action.invoke()
        },
        shape = RoundedCornerShape(15.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            contentColor = Color.Black
        ),
    ) {
        Text(text = text, fontSize = 18.sp)
        Spacer(modifier = Modifier.width(10.dp))
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(ButtonDefaults.IconSize)
        )
    }
}


/**
 * 状态集合（对状态的统一管理）
 */
@OptIn(ExperimentalMaterial3Api::class)
class StateHolder(
    val resultLauncher: ActivityResultLauncher<Intent>,
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
    val showDeleteDialog: MutableState<Boolean>
)

/**
 * 返回StateHolder类型（需要添加@Composable注解，才能使用remember{...}，但是本身并不是一个真正用于显示的可组合函数）
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberStates(
    resultLauncher: ActivityResultLauncher<Intent>,
    currentScreen: MutableState<Screen> = remember { mutableStateOf(Screen.QueryPage) },
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.LearnPage.route,
    scope: CoroutineScope = rememberCoroutineScope(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    dropState: MutableState<Boolean> = mutableStateOf(false),
    showDeleteDialog: MutableState<Boolean> = remember { mutableStateOf(false) }
) = StateHolder(
    resultLauncher,
    currentScreen,
    navController,
    startDestination,
    scope,
    drawerState,
    dropState,
    showDeleteDialog
)

val screens = listOf(Screen.QueryPage, Screen.ListenPage, Screen.LearnPage)

/**
 *Screen类（与用于显示的Screen实体不同！要区分开！Screen类只用于提供页面需要的元数据metaData：icon、title、"route"【用于导航】）
 */
sealed class Screen(val route: String, val title: String, val icon: Int) {
    object QueryPage :
        Screen(route = "query", title = "查词", icon = R.drawable.trans)

    object ListenPage :
        Screen(route = "listen", title = "听力", icon = R.drawable.listen)

    object LearnPage :
        Screen(route = "learn", title = "学习", icon = R.drawable.dictionary)
}

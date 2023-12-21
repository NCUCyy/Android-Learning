package com.cyy.transapp.activity.main.view

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cyy.transapp.R
import com.cyy.transapp.TransApp
import com.cyy.transapp.activity.main.StateHolder
import com.cyy.transapp.activity.other.WordActivity
import com.cyy.transapp.model.Vocabulary
import com.cyy.transapp.view_model.CurUserViewModel
import com.cyy.transapp.view_model.CurUserViewModelFactory
import kotlinx.coroutines.launch


/**
 * Drawer（侧滑菜单） + NavGraph（页面主体）
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerView(
    states: StateHolder,
    userId: Int,
    vocabulary: Vocabulary,
) {
    val application = LocalContext.current.applicationContext as TransApp
    // MainActivity中管理User的ViewModel
    val curUserViewModel = viewModel<CurUserViewModel>(
        factory = CurUserViewModelFactory(
            userId,
            application.userRepository,
        )
    )
    Log.i("vocabulary-------", vocabulary.desc)
    val curUser = curUserViewModel.curUser.collectAsStateWithLifecycle()
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
                    Icon(
                        painter = painterResource(id = curUser.value.iconId),
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            states.scope.launch {
                                states.drawerState.close()
                            }
                        }
                    )
                    Text(text = "用户名", fontSize = 30.sp)
                }
                NavigationDrawerItem(
                    label = {
                        Text("我的单词本", fontSize = 20.sp)
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.book),
                            tint = Color.DarkGray,
                            contentDescription = "我的单词本",
                        )
                    },
                    selected = false,
                    onClick = {
                        states.scope.launch {
                            states.drawerState.close()
                        }
                        val intent = Intent(states.navController.context, WordActivity::class.java)
                        intent.putExtra("vocabulary", curUser.value.vocabulary)
                        states.resultLauncher.launch(intent)
                    })
                NavigationDrawerItem(
                    label = {
                        Text("清空翻译记录", fontSize = 20.sp)
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.delete_history),
                            tint = Color.DarkGray,
                            contentDescription = "我的单词本",
                        )
                    },
                    selected = false,
                    onClick = {
                        states.scope.launch {
                            states.drawerState.close()
                        }
                        states.showDeleteDialog.value = true
                    })

            }
        },
        content = {
            // 主体是导航图
            NavigationGraphScreen(states, userId, vocabulary)
        })
}
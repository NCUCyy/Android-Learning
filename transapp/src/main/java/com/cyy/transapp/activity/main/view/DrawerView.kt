package com.cyy.transapp.activity.main.view

import android.app.Activity
import android.content.Intent
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cyy.transapp.R
import com.cyy.transapp.TransApp
import com.cyy.transapp.activity.main.StateHolder
import com.cyy.transapp.activity.other.AvatarImage
import com.cyy.transapp.activity.other.StarWordActivity
import com.cyy.transapp.activity.other.SystemSettingActivity
import com.cyy.transapp.activity.other.UserSettingActivity
import com.cyy.transapp.view_model.CurUserViewModel
import com.cyy.transapp.view_model.CurUserViewModelFactory


/**
 * Drawer（侧滑菜单） + NavGraph（页面主体）
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerView(
    states: StateHolder,
    userId: Int,
    vocabulary: String,
) {
    val context = LocalContext.current as Activity
    val application = LocalContext.current.applicationContext as TransApp
    // MainActivity中管理User的ViewModel
    val curUserViewModel = viewModel<CurUserViewModel>(
        factory = CurUserViewModelFactory(
            userId,
            application.userRepository,
        )
    )
    val curUser = curUserViewModel.curUser.collectAsStateWithLifecycle()
    ModalNavigationDrawer(
        // 抽屉是否可以通过手势进行交互
        gesturesEnabled = true,
        // 抽屉打开后，遮挡内容的蒙层的颜色
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
                    .width(350.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(bottom = 30.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(modifier = Modifier.clickable {
                        val intent = Intent(context, UserSettingActivity::class.java)
                        intent.putExtra("userId", userId)
                        states.resultLauncher.launch(intent)
                    }) {
                        AvatarImage(
                            avatar = curUser.value.avatar,
                            avatarSize = 100.dp,
                            borderWidth = 4.dp
                        )
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = curUser.value.username,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        style = TextStyle(
                            shadow = Shadow(
                                color = Color.Gray,
                                offset = Offset(5.0f, 5.0f),
                                blurRadius = 3f
                            )
                        )
                    )
                }
                Card(
                    modifier = Modifier.padding(
                        start = 20.dp,
                        end = 20.dp,
                        top = 5.dp,
                        bottom = 20.dp
                    ),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent,
                    ),
                ) {
                    // TODO：0、个人信息
                    NavigationDrawerItem(
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedContainerColor = Color(0xFFFCE7EE)
                        ),
                        shape = RoundedCornerShape(0.dp, 0.dp, 0.dp, 0.dp),
                        label = {
                            Text("个人信息", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.person_edit),
                                tint = Color.DarkGray,
                                contentDescription = null,
                                modifier = Modifier.size(25.dp)
                            )
                        },
                        selected = false,
                        onClick = {
                            val intent =
                                Intent(
                                    states.navController.context,
                                    UserSettingActivity::class.java
                                )
                            states.resultLauncher.launch(intent)
                        })
                    Divider(thickness = 1.dp)
                    // TODO：1、我的单词本
                    NavigationDrawerItem(
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedContainerColor = Color(0xFFFCE7EE)
                        ),
                        shape = RoundedCornerShape(0.dp, 0.dp, 0.dp, 0.dp),
                        label = {
                            Text("我的生词本", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.book),
                                tint = Color.DarkGray,
                                contentDescription = null,
                                modifier = Modifier.size(25.dp)
                            )
                        },
                        selected = false,
                        onClick = {
                            val intent =
                                Intent(states.navController.context, StarWordActivity::class.java)
                            intent.putExtra("userId", userId)
                            states.resultLauncher.launch(intent)
                        })
                    Divider(thickness = 1.dp)
                    // TODO：2、清空翻译记录
                    NavigationDrawerItem(
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedContainerColor = Color(0xFFFCE7EE)
                        ),
                        shape = RoundedCornerShape(0.dp, 0.dp, 0.dp, 0.dp),
                        label = {
                            Text("清空翻译记录", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.delete_history),
                                tint = Color.DarkGray,
                                contentDescription = null,
                                modifier = Modifier.size(25.dp)
                            )
                        },
                        selected = false,
                        onClick = {
                            // 弹出确认删除的对话框
                            states.showDeleteDialog.value = true
                        })
                    Divider(thickness = 1.dp)
                    // TODO：3、更换单词本
                    NavigationDrawerItem(
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedContainerColor = Color(0xFFFCE7EE)
                        ),
                        shape = RoundedCornerShape(0.dp, 0.dp, 0.dp, 0.dp),
                        label = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("正在学习", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = curUser.value.vocabulary,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.W900,
                                        color = Color.Gray
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Icon(
                                        painter = painterResource(id = R.drawable.right),
                                        contentDescription = null,
                                        tint = Color.Gray,
                                    )
                                }
                            }
                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.dictionary),
                                tint = Color.DarkGray,
                                contentDescription = null,
                                modifier = Modifier.size(25.dp)
                            )
                        },
                        selected = false,
                        onClick = {
                            // 前往VocabularyActivity
                            toVocabularySettingActivity(
                                userId,
                                curUser.value.vocabulary,
                                context,
                                states.resultLauncher
                            )
                        })
                    Divider(thickness = 1.dp)
                    // TODO：4、设置
                    NavigationDrawerItem(
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedContainerColor = Color(0xFFFCE7EE)
                        ),
                        shape = RoundedCornerShape(0.dp, 0.dp, 0.dp, 0.dp),
                        label = {
                            Text("设置", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.settings),
                                tint = Color.DarkGray,
                                contentDescription = null,
                                modifier = Modifier.size(25.dp)
                            )
                        },
                        selected = false,
                        onClick = {
                            val intent =
                                Intent(
                                    states.navController.context,
                                    SystemSettingActivity::class.java
                                )
                            states.resultLauncher.launch(intent)
                        })


                }
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF10656),
                            contentColor = Color.White
                        ),
                        onClick = {
                            context.finish()
                        }) {
                        Text(
                            text = "退出登录",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                }
            }
        },
        content = {
            // 主体是导航图
            NavigationGraphScreen(states, userId, vocabulary)
        })
}

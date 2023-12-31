package com.cyy.transapp.activity.other

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cyy.transapp.R
import com.cyy.transapp.TransApp
import com.cyy.transapp.activity.main.rainbowColorsBrush
import com.cyy.transapp.model.UsernameState
import com.cyy.transapp.util.FileUtil
import com.cyy.transapp.view_model.user.UserSettingViewModel
import com.cyy.transapp.view_model.user.UserSettingViewModelFactory

/**
 * 用户信息活动
 */
class UserSettingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userId = intent.getIntExtra("userId", 1)
        setContent {
            UserMainScreen(userId)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserMainScreen(userId: Int) {
    val context = LocalContext.current as Activity
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // TODO：显示查询的词汇
                    Text(
                        text = "个人信息",
                        fontWeight = FontWeight.Bold,
                        fontSize = (20 + syncFontSize.value).sp,
                    )
                },
                // 左侧图标
                navigationIcon = {
                    // 图标按钮
                    IconButton(onClick = {
                        // TODO：返回
                        context.finish()
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.back),
                            contentDescription = null,
                        )
                    }
                },
                actions = {
                }
            )
        },
        bottomBar = {
        },
        content = {
            // 页面的主体部分
            Box(modifier = Modifier.padding(it)) {
                UserScreen(userId)
            }
        },
        floatingActionButton = {
        })
}

@Preview
@Composable
fun UserScreen(userId: Int = 1) {
    val context = LocalContext.current as Activity
    val application = LocalContext.current.applicationContext as TransApp
    val userSettingViewModel = viewModel<UserSettingViewModel>(
        factory = UserSettingViewModelFactory(
            userId,
            context,
            application.userRepository
        )
    )
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                // 更新头像
                userSettingViewModel.updateAvatar(it)
                Toast.makeText(context, "修改成功！", Toast.LENGTH_SHORT).show()
            }
        }
    )
    val curUser = userSettingViewModel.curUser.collectAsStateWithLifecycle()
    val username = userSettingViewModel.username.collectAsState()
    val password = userSettingViewModel.password.collectAsState()
    val profile = userSettingViewModel.profile.collectAsState()
    val isEdit = userSettingViewModel.isEdit.collectAsState()
    val usernameState = userSettingViewModel.usernameState.collectAsState().value
    val avatar = userSettingViewModel.avatar.collectAsState()
    val showFullAvatar = remember { mutableStateOf(false) }
    if (showFullAvatar.value) {
        FullAvatarDialog(showFullAvatar, avatar.value)
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(modifier = Modifier
                .size(150.dp)
                .clickable {
                    // TODO：选择头像
                    if (isEdit.value) {
                        // 在编辑，则打开相册
                        galleryLauncher.launch("image/*")
                    } else {
                        // 不在编辑，则显示完整头像
                        showFullAvatar.value = true
                    }
                }) {
                AvatarImage(
                    avatar = avatar.value,
                    avatarSize = 150.dp,
                    borderWidth = 4.dp
                )
                Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier.fillMaxSize()) {
                    if (isEdit.value) {
                        Icon(
                            painter = painterResource(id = R.drawable.edit),
                            contentDescription = null
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.edit_off),
                            contentDescription = null
                        )
                    }
                }
            }

        }
        Spacer(modifier = Modifier.height(20.dp))
        Card(
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
            modifier = Modifier
                .fillMaxSize(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(
                topStart = 40.dp,
                topEnd = 40.dp,
                bottomEnd = 0.dp,
                bottomStart = 0.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(80.dp))
                TextFieldCard(
                    username.value,
                    userSettingViewModel::updateUsername,
                    label = {
                        Text(text = "用户名", fontWeight = FontWeight.Bold)
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.person),
                            contentDescription = null,
                        )
                    },
                    isEdit.value,
                    trailingIcon = {
                        // 若已开始输入
                        if (usernameState != UsernameState.NOT_BEGIN) {
                            if (usernameState in listOf(
                                    UsernameState.EXIST,
                                    UsernameState.EMPTY
                                )
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "用户名已存在", color = Color.Red)
                                    Icon(
                                        painter = painterResource(id = R.drawable.error),
                                        contentDescription = null,
                                        tint = Color.Red,
                                        modifier = Modifier.padding(
                                            start = 8.dp,
                                            end = 8.dp
                                        )
                                    )
                                }

                            } else {
                                Icon(
                                    painter = painterResource(id = R.drawable.correct),
                                    contentDescription = null,
                                    tint = Color(0xFF08A808)
                                )
                            }
                        }
                    })
                Spacer(modifier = Modifier.height(20.dp))
                TextFieldCard(
                    password.value,
                    userSettingViewModel::updatePassword,
                    label = {
                        Text(text = "密码", fontWeight = FontWeight.Bold)
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.password),
                            contentDescription = null
                        )
                    },
                    isEdit.value
                )
                Spacer(modifier = Modifier.height(20.dp))
                TextFieldCard(
                    profile.value,
                    userSettingViewModel::updateProfile,
                    label = {
                        Text(text = "个人介绍", fontWeight = FontWeight.Bold)
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.profile),
                            contentDescription = null
                        )
                    },
                    isEdit.value
                )
                Spacer(modifier = Modifier.height(40.dp))
                if (isEdit.value) {
                    Button(
                        elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(
                                0xFFE91E63
                            )
                        ),
                        shape = RoundedCornerShape(15.dp),
                        onClick = {
                            // 提示
                            if (usernameState == UsernameState.AVAILABLE || usernameState == UsernameState.NOT_BEGIN) {
                                Toast.makeText(context, "修改成功！", Toast.LENGTH_SHORT)
                                    .show()
                            } else {
                                Toast.makeText(context, "修改失败！", Toast.LENGTH_SHORT)
                                    .show()
                            }
                            // 保存修改
                            userSettingViewModel.saveEdit()
                        }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "确认修改",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Icon(
                                painter = painterResource(id = R.drawable.person_check),
                                contentDescription = null
                            )
                        }
                    }
                } else {
                    Button(
                        elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF77FFD1),
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(15.dp),
                        onClick = {
                            // 开始编辑
                            userSettingViewModel.beginEdit()
                        }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "编辑信息",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Icon(
                                painter = painterResource(id = R.drawable.person_edit),
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AvatarImage(avatar: String, avatarSize: Dp = 36.dp, borderWidth: Dp) {
    if (avatar == "") {
        Image(
            painter = painterResource(id = R.drawable.face),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .border(
                    BorderStroke(borderWidth, rainbowColorsBrush),
                    CircleShape
                )
                .size(avatarSize)
                .clip(CircleShape),
            colorFilter = ColorFilter.tint(Color.Black)
        )
    } else {
        Image(
            bitmap = FileUtil.stringToImageBitmap(avatar),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .border(
                    BorderStroke(borderWidth, rainbowColorsBrush),
                    CircleShape
                )
                .size(avatarSize)
                .clip(CircleShape)
        )
    }
}

@Composable
fun FullAvatarDialog(showFullAvatar: MutableState<Boolean>, avatar: String) {
    Dialog(onDismissRequest = { showFullAvatar.value = false }) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Card(
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White,
                ),
            ) {
                if (avatar == "") {
                    Image(
                        painter = painterResource(id = R.drawable.face),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        colorFilter = ColorFilter.tint(Color.Black),
                        modifier = Modifier
                            .fillMaxSize()
                            .size(300.dp)
                    )
                } else {
                    Image(
                        bitmap = FileUtil.stringToImageBitmap(avatar),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(300.dp)
                            .clip(RoundedCornerShape(10.dp, 10.dp, 10.dp, 10.dp))
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextFieldCard(
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable () -> Unit = {},
    leadingIcon: @Composable (() -> Unit),
    isEdit: Boolean,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    val textColor = remember { mutableStateOf(Color.Black) }
    if (!isEdit) {
        textColor.value = Color.Gray
    } else {
        textColor.value = Color.Black
    }
    Card(
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
        modifier = Modifier
            .size(width = 300.dp, height = Dp.Infinity),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        TextField(
            textStyle = TextStyle(
                fontSize = 18.sp,
                color = textColor.value,
            ),
            readOnly = !isEdit,
            value = value,
            onValueChange = onValueChange,
            leadingIcon = leadingIcon,
            label = label,
            trailingIcon = trailingIcon,
            colors = ExposedDropdownMenuDefaults.textFieldColors(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
            ),
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}
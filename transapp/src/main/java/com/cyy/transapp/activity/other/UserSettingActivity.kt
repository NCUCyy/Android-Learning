package com.cyy.transapp.activity.other

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cyy.transapp.R
import com.cyy.transapp.TransApp
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

@Composable
fun UserMainScreen(userId: Int) {
    UserScreen(userId)
}

@Preview
@Composable
fun UserScreen(userId: Int = 1) {
    val application = LocalContext.current.applicationContext as TransApp
    val userSettingViewModel = viewModel<UserSettingViewModel>(
        factory = UserSettingViewModelFactory(
            userId,
            application.userRepository
        )
    )
    val curUser = userSettingViewModel.curUser.collectAsStateWithLifecycle()
    val username = userSettingViewModel.username.collectAsState()
    val password = userSettingViewModel.password.collectAsState()
    val profile = userSettingViewModel.profile.collectAsState()
    val isEdit = remember { mutableStateOf(false) }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Log.i("DrawerView", "DrawerView: ${curUser.value.iconId}")
            Icon(
                painter = painterResource(id = curUser.value.iconId),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
            )
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
                    TextFieldCard(username.value, userSettingViewModel::updateUsername, label = {
                        Text(text = "用户名")
                    }, leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.person),
                            contentDescription = null,
                            modifier = Modifier.clickable {
                                // TODO: 更换头像（待完成）
                            }
                        )
                    }, isEdit)
                    Spacer(modifier = Modifier.height(20.dp))
                    TextFieldCard(password.value, userSettingViewModel::updatePassword, label = {
                        Text(text = "密码")
                    }, leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.password),
                            contentDescription = null
                        )
                    }, isEdit)
                    Spacer(modifier = Modifier.height(20.dp))
                    TextFieldCard(
                        profile.value,
                        userSettingViewModel::updateProfile,
                        label = {
                            Text(text = "个人介绍")
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.profile),
                                contentDescription = null
                            )
                        },
                        isEdit
                    )
                    Spacer(modifier = Modifier.height(40.dp))
                    if (isEdit.value) {
                        Button(
                            elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63)),
                            shape = RoundedCornerShape(15.dp),
                            onClick = {
                                isEdit.value = false
                            }) {
                            Text(
                                text = "确认修改",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                            )
                        }
                    } else {
                        Button(
                            elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF66F8C7),
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(15.dp),
                            onClick = {
                                isEdit.value = true
                            }) {
                            Text(
                                text = "编辑信息",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                            )
                        }
                    }
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
    isEdit: MutableState<Boolean>
) {
    Card(
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
        modifier = Modifier
            .size(width = 300.dp, height = Dp.Infinity),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        TextField(
            textStyle = TextStyle(
                fontSize = 18.sp,
            ),
            readOnly = !isEdit.value,
            value = value,
            onValueChange = onValueChange,
            leadingIcon = leadingIcon,
            label = label,
            trailingIcon = {
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
            ),
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}
package com.cyy.exp2.psychological_test.ui

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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cyy.exp2.psychological_test.PsychologicalTestApp
import com.cyy.exp2.psychological_test.pojo.User
import com.cyy.exp2.psychological_test.view_model.LoginViewModel
import com.cyy.exp2.psychological_test.view_model.UserViewModel
import com.cyy.exp2.psychological_test.view_model.UserViewModelFactory

/**
 * 显式意图跳转到TestActivity，并携带userId------>用于创建RecordViewModel
 */
class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 使用ActivityResultLauncher进行意图跳转
        val resultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            // 意图结束后，执行这个「回调函数」
            ActivityResultCallback {
                if (it.resultCode == RESULT_OK) {
                    // 返回的data数据是个intent类型，里面存储了一段文本内容
                    val username = it.data?.getStringExtra("username")
                    Toast.makeText(this, "$username 退出成功！", Toast.LENGTH_LONG).show()
                }
            }
        )
        setContent {
            LoginScreen(resultLauncher)
        }
    }
}

/**
 * 使用livedata进行监视：若数据发生改变，则执行操作
 */
@SuppressLint("UnrememberedMutableState")
// 登录界面
@Composable
fun LoginScreen(resultLauncher: ActivityResultLauncher<Intent>) {
    val context = LocalContext.current
    val application = LocalContext.current.applicationContext as PsychologicalTestApp
    val loginViewModel: LoginViewModel = viewModel()
    var username = loginViewModel.username.collectAsState()
    var password = loginViewModel.password.collectAsState()
    val isRegister = loginViewModel.isRegister.collectAsState()

    val userViewModel = viewModel<UserViewModel>(
        factory = UserViewModelFactory(
            application.userRepository,
        )
    )
    // 监听登录状态！---使用LiveData而不是StateFlow
    userViewModel.loginRes.observe(context as ComponentActivity) {
        if (it) {
            Toast.makeText(application, "登录成功", Toast.LENGTH_SHORT).show()
            // 登录成功后，将登录信息清空
            loginViewModel.afterLogin()
            // 跳转到TestActivity(并携带userId，表示登录的用户)
            Log.i("试试-LoginActivity", userViewModel.loginUser.value.toString())

            val intent = Intent(context as Activity, MainActivity::class.java)
            intent.putExtra("userId", userViewModel.loginUser.value?.id)
            resultLauncher.launch(intent)
        } else {
            Toast.makeText(application, "用户名或密码错误", Toast.LENGTH_SHORT).show()
        }
    }
    // 监听注册状态！---使用LiveData而不是StateFlow
    userViewModel.registerRes.observe(context as ComponentActivity) {
        if (it) {
            loginViewModel.updateIsRegister(false)
            // 注册成功后，将注册信息复制到登录信息
            loginViewModel.afterRegister()
            Toast.makeText(application, "注册成功", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(application, "用户名已存在", Toast.LENGTH_SHORT).show()
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE7F2FC)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 100.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.FavoriteBorder,
                contentDescription = "App Icon",
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(8.dp)) // 添加一些间距
            Text(
                text = "每日单词 App",
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 60.sp,
                    fontStyle = FontStyle.Italic
                ),
            )
        }

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Card(
                modifier = Modifier
                    .padding(40.dp),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White,
                ),
            ) {
                ScreenTitle(title = "登录")
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    color = Color(0xFFC4C4C4),
                )
                Spacer(modifier = Modifier.height(30.dp))
                InputBox(username, "用户名", action = loginViewModel::updateUsername)
                Spacer(modifier = Modifier.height(25.dp))
                InputBox(password, "密码", action = loginViewModel::updatePassword)
                Spacer(modifier = Modifier.height(30.dp))
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    Row(modifier = Modifier.padding(bottom = 15.dp)) {
                        Button(
                            modifier = Modifier.padding(end = 15.dp),
                            onClick = {
                                /*TODO*/
                                userViewModel.login(username.value, password.value)
                            },
                            elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 10.dp)
                        ) {
                            Text(text = "登录")
                        }
                        Button(
                            onClick = {
                                /*TODO*/
                                loginViewModel.updateIsRegister(true)
                            },
                            elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 10.dp)
                        ) {
                            Text(text = "注册")
                        }
                    }
                }
            }
        }
    }
    // 注册对话框---用showDialog控制是否显示
    RegisterScreen(
        isRegister,
        loginViewModel
    ) { username, password, sex ->
        userViewModel.register(User(username, password, sex))
    }
}

@Composable
fun RegisterScreen(
    showDialog: State<Boolean>,
    loginViewModel: LoginViewModel,
    onRegister: (username: String, password: String, sex: String) -> Unit
) {
    val options = listOf("男", "女")
    var username = loginViewModel.registerUsername.collectAsState()
    var password = loginViewModel.registerPassword.collectAsState()
    var sex = loginViewModel.registerSex.collectAsState()

    if (showDialog.value) {
        Dialog(onDismissRequest = {
            loginViewModel.updateIsRegister(false)
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
                ) {
                    ScreenTitle(title = "注册")
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        color = Color(0xFFC4C4C4),
                    )

                    Spacer(modifier = Modifier.height(30.dp))
                    InputBox(
                        input = username,
                        placeHolder = "用户名",
                        action = loginViewModel::updateRegisterUsername
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    InputBox(
                        input = password,
                        placeHolder = "密码",
                        action = loginViewModel::updateRegisterPassword
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(), horizontalArrangement = Arrangement.Center
                        ) {
                            options.forEach {
                                RadioButton(
                                    selected = (it == sex.value),
                                    onClick = {
                                        loginViewModel.updateRegisterSex(it)
                                    }
                                )
                                Text(
                                    fontSize = 16.sp,
                                    text = it,
                                    modifier = Modifier.padding(top = 10.dp, end = 20.dp)
                                )
                            }
                        }

                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(
                            elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 10.dp),
                            onClick = {
                                onRegister(username.value, password.value, sex.value)
                            }) {
                            Text("注册")
                        }
                    }
                }
            }
        }
    }
}


/**
 * 用到了状态提升！！！
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputBox(input: State<String>, placeHolder: String, action: (String) -> Unit) {
    Box(
        modifier = Modifier.padding(
            start = 20.dp,
            end = 20.dp,
        )
    ) {
        Card(elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp)) {
            TextField(
                value = input.value,
                modifier = Modifier
                    .fillMaxWidth(),
                onValueChange = {
                    action(it)
                },
                label = { Text(text = placeHolder) },
                placeholder = { Text(text = "请输入${placeHolder}...") },
                singleLine = true, // 单行文本框
                shape = MaterialTheme.shapes.extraSmall, // 设置边框形状
                textStyle = TextStyle.Default.copy(color = Color.Black), // 设置文本颜色
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    placeholderColor = Color.Gray,
                ),
            )
        }
    }
}

@Composable
fun ScreenTitle(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 26.dp)
    ) {
        Text(
            text = title,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 10.dp, top = 20.dp),
            color = Color(0xFF4E403C)
        )
    }
}
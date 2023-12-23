package com.cyy.transapp.activity.index

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cyy.transapp.R
import com.cyy.transapp.TransApp
import com.cyy.transapp.activity.main.MainActivity
import com.cyy.transapp.model.ConfirmPasswordState
import com.cyy.transapp.model.RegisterState
import com.cyy.transapp.model.UsernameState
import com.cyy.transapp.pojo.User
import com.cyy.transapp.view_model.user.UserViewModel
import com.cyy.transapp.view_model.user.UserViewModelFactory

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 使用ActivityResultLauncher进行意图跳转
        val resultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            // 意图结束后，执行这个「回调函数」
            ActivityResultCallback {
                this.finish()
            }
        )
        setContent {
            RegisterMainScreen(resultLauncher)
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterMainScreen(resultLauncher: ActivityResultLauncher<Intent>) {
    val context = LocalContext.current as Activity
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // TODO：显示查询的词汇
                    Text(
                        text = "Back",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        maxLines = 1
                    )
                },
                // 左侧图标
                navigationIcon = {
                    // 图标按钮
                    IconButton(onClick = {
                        // TODO：返回Index
                        context.finish()
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.back),
                            contentDescription = null,
                        )
                    }
                },
            )
        },
        content = {
            // 页面的主体部分
            RegisterScreen(resultLauncher)
        },
        floatingActionButton = {
        })
}

/**
 * 通用登录函数---loginToMainActivity/register都要用
 */
fun loginToMainActivity(
    resultLauncher: ActivityResultLauncher<Intent>,
    context: Activity,
    loginUser: User
) {
    val intent = Intent(context, MainActivity::class.java)
    intent.putExtra("userId", loginUser.id)
    resultLauncher.launch(intent)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(resultLauncher: ActivityResultLauncher<Intent>) {
    val application = LocalContext.current.applicationContext as TransApp
    val userViewModel =
        viewModel<UserViewModel>(
            factory = UserViewModelFactory(
                application.userRepository,
                application.planRepository
            )
        )
    // 输入的用户名、密码、确认密码
    val username = userViewModel.username.collectAsState()
    val password = userViewModel.password.collectAsState()
    val confirmPassword = userViewModel.confirmPassword.collectAsState()

    // 用户名、确认密码的状态（必须要有NOT_BEGIN）
    val usernameState = userViewModel.usernameState.collectAsState().value
    val confirmPasswordState = userViewModel.confirmPasswordState.collectAsState().value

    // 登录（必须要有NOT_BEGIN）
    val registerState = userViewModel.registerState
    val context = LocalContext.current as ComponentActivity

    // TODO：点击注册按钮---onClick后，修改registerState(观察registerState的改变)
    registerState.observe(context) {
        if (registerState.value == RegisterState.SUCCESS) {
            val loginUser = userViewModel.loginUser
            // TODO：loginToMainActivity
            loginToMainActivity(resultLauncher, context, loginUser)
            // 提示登录成功
            Toast.makeText(context, registerState.value!!.desc, Toast.LENGTH_LONG).show()
            // 必须恢复为NOT_BEGIN
            registerState.value = RegisterState.NOT_BEGIN
        } else if (registerState.value == RegisterState.FAILED) {
            Toast.makeText(context, registerState.value!!.desc, Toast.LENGTH_LONG).show()
            registerState.value = RegisterState.NOT_BEGIN
        }
    }


    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Welcome！", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "Sign up", fontSize = 50.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(50.dp))

        // 1、用户名
        TextFieldCard(
            shape = RoundedCornerShape(20.dp, 20.dp, 0.dp, 0.dp),
            label = { Text(text = "Username") },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.person),
                    contentDescription = null
                )
            },
            trailingIcon = {// 若已开始输入
                if (usernameState != UsernameState.NOT_BEGIN) {
                    if (usernameState in listOf(UsernameState.EXIST, UsernameState.EMPTY)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = usernameState.desc, color = Color.Red)
                            Icon(
                                painter = painterResource(id = R.drawable.error),
                                contentDescription = null,
                                tint = Color.Red,
                                modifier = Modifier.padding(start = 8.dp, end = 8.dp)
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
            },
            value = username.value,
            onValueChange = userViewModel::updateUsername,
            visualTransformation = VisualTransformation.None
        )
        Spacer(modifier = Modifier.height(10.dp))
        // 2、密码
        TextFieldCard(
            shape = RoundedCornerShape(0.dp, 0.dp, 0.dp, 0.dp),
            label = {
                Text(text = "Password")
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.password),
                    contentDescription = null
                )
            },
            trailingIcon = {},
            value = password.value,
            onValueChange = userViewModel::updatePassword,
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(10.dp))
        // 3、确认密码
        TextFieldCard(
            shape = RoundedCornerShape(0.dp, 0.dp, 20.dp, 20.dp),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.confirm_password),
                    contentDescription = null
                )
            },
            value = confirmPassword.value,
            onValueChange = userViewModel::updateConfirmPassword,
            label = { Text(text = "Confirm Password") },
            trailingIcon = {
                // 若已开始输入
                if (confirmPasswordState != ConfirmPasswordState.NOT_BEGIN) {
                    if (confirmPasswordState == ConfirmPasswordState.DIFFERENT) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = confirmPasswordState.desc)
                            Icon(
                                painter = painterResource(id = R.drawable.error),
                                contentDescription = null,
                                tint = Color.Red,
                                modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                            )
                        }
                    } else
                        Icon(
                            painter = painterResource(id = R.drawable.correct),
                            contentDescription = null,
                            tint = Color(0xFF08A808)
                        )
                }
            },
            visualTransformation = PasswordVisualTransformation(),
        )
        Spacer(modifier = Modifier.height(60.dp))
        Button(
            elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
            onClick = {
                userViewModel.register()
            }) {
            Text(
                text = "Done",
                fontSize = 17.sp,
                modifier = Modifier.padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 10.dp,
                    bottom = 10.dp
                ),
            )
        }
    }
}
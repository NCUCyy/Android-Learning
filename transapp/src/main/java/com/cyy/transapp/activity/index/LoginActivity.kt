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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cyy.transapp.R
import com.cyy.transapp.TransApp
import com.cyy.transapp.model.LoginState
import com.cyy.transapp.model.UsernameAndPasswordState
import com.cyy.transapp.view_model.user.UserViewModel
import com.cyy.transapp.view_model.user.UserViewModelFactory

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val isLogout = mutableStateOf(false)
        // 使用ActivityResultLauncher进行意图跳转
        val resultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            // 意图结束后，执行这个「回调函数」
            ActivityResultCallback {
            }
        )
        setContent {
            LoginMainScreen(resultLauncher)
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginMainScreen(resultLauncher: ActivityResultLauncher<Intent>) {
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
            LoginScreen(resultLauncher)
        },
        floatingActionButton = {
        })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(resultLauncher: ActivityResultLauncher<Intent>) {
    val application = LocalContext.current.applicationContext as TransApp
    val userViewModel =
        viewModel<UserViewModel>(
            factory = UserViewModelFactory(
                application.userRepository,
                application.planRepository
            )
        )
    val username = userViewModel.username.collectAsState()
    val password = userViewModel.password.collectAsState()
    val usernameAndPasswordState = userViewModel.usernameAndPasswordState.collectAsState().value
    val loginState = userViewModel.loginState
    val context = LocalContext.current as ComponentActivity


    loginState.observe(context) {
        if (loginState.value == LoginState.SUCCESS) {
            val loginUser = userViewModel.loginUser
            // TODO：loginToMainActivity
            loginToMainActivity(resultLauncher, context, loginUser)
            // 提示登录成功
            Toast.makeText(context, loginState.value!!.desc, Toast.LENGTH_LONG).show()
            loginState.value = LoginState.NOT_BEGIN
        } else if (loginState.value == LoginState.FAILED) {
            Toast.makeText(context, loginState.value!!.desc, Toast.LENGTH_LONG).show()
            loginState.value = LoginState.NOT_BEGIN
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = null,
                modifier = Modifier.size(38.dp),
                tint = Color(0xFF2cb74d)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Trans",
                fontSize = 30.sp,
                fontWeight = FontWeight.W900,
                fontFamily = FontFamily.Serif,
                fontStyle = FontStyle.Italic
            )
        }
        Spacer(modifier = Modifier.height(40.dp))
        Text(text = "Welcome！", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "Sign in", fontSize = 50.sp, fontWeight = FontWeight.Bold)
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
            trailingIcon = {},
            value = username.value,
            onValueChange = userViewModel::updateUsername,
            visualTransformation = VisualTransformation.None
        )
        Spacer(modifier = Modifier.height(10.dp))
        // 2、密码
        TextFieldCard(
            shape = RoundedCornerShape(0.dp, 0.dp, 20.dp, 20.dp),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.password),
                    contentDescription = null
                )
            },
            trailingIcon = {
                if (usernameAndPasswordState != UsernameAndPasswordState.NOT_BEGIN) {
                    if (usernameAndPasswordState == UsernameAndPasswordState.ERROR) {
                        Row {
                            Text(text = usernameAndPasswordState.desc, color = Color.Red)
                            Icon(
                                painter = painterResource(id = R.drawable.error),
                                contentDescription = null,
                                tint = Color.Red,
                                modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                            )
                        }
                    } else if (usernameAndPasswordState == UsernameAndPasswordState.CORRECT) {
                        Icon(
                            painter = painterResource(id = R.drawable.correct),
                            contentDescription = null,
                            tint = Color(0xFF08A808)
                        )
                    }
                }
            },
            onValueChange = userViewModel::updatePassword,
            value = password.value,
            label = { Text(text = "Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(60.dp))
        Button(
            elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
            onClick = {
                userViewModel.login()
            }) {
            Text(
                text = "Sign in now",
                fontSize = 17.sp,
                modifier = Modifier.padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 10.dp,
                    bottom = 10.dp
                ),
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Sign up",
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            modifier = Modifier.clickable {
                // 跳转到注册界面
                val intent = Intent(context, RegisterActivity::class.java)
                resultLauncher.launch(intent)
            })
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextFieldCard(
    shape: RoundedCornerShape,
    label: @Composable () -> Unit,
    leadingIcon: @Composable () -> Unit,
    trailingIcon: @Composable () -> Unit,
    value: String,
    onValueChange: (String) -> Unit,
    visualTransformation: VisualTransformation
) {
    Card(
        modifier = Modifier.size(width = 300.dp, height = 80.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFDDDDDD),
        )
    ) {
        TextField(
            maxLines = 1,
            modifier = Modifier.fillMaxSize(),
            textStyle = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
            ),
            value = value,
            onValueChange = onValueChange,
            label = label,
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledLabelColor = Color.Black,
                focusedLabelColor = Color.Black
            ),
            leadingIcon = leadingIcon,
            visualTransformation = visualTransformation,
            trailingIcon = trailingIcon
        )
    }
}
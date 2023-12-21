package com.cyy.transapp.activity.index

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
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
                this.finish()
            }
        )
        setContent {
            if (isLogout.value) {
                this.finish()
            } else {
                LoginScreen(resultLauncher)
            }
        }
    }
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
    Column {
        TextField(
            value = username.value,
            onValueChange = userViewModel::updateUsername,
            label = { Text(text = "用户名") },
            colors = TextFieldDefaults.textFieldColors()
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = password.value,
                onValueChange = userViewModel::updatePassword,
                label = { Text(text = "密码") },
                colors = TextFieldDefaults.textFieldColors(),
                trailingIcon = {
                    if (usernameAndPasswordState != UsernameAndPasswordState.NOT_BEGIN) {
                        if (usernameAndPasswordState == UsernameAndPasswordState.ERROR) {
                            Icon(
                                painter = painterResource(id = R.drawable.error),
                                contentDescription = null,
                                tint = Color.Red,
                                modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                            )
                        } else if (usernameAndPasswordState == UsernameAndPasswordState.CORRECT) {
                            Icon(
                                painter = painterResource(id = R.drawable.correct),
                                contentDescription = null,
                                tint = Color(0xFF08A808)
                            )
                        }
                    }
                }
            )
            Text(text = usernameAndPasswordState.desc)
        }
        Button(onClick = {
            userViewModel.login()
        }) {
            Text(text = "登录")
        }
    }
}
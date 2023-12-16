package com.cyy.transapp.activity

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cyy.transapp.R
import com.cyy.transapp.TransApp
import com.cyy.transapp.model.ConfirmPasswordState
import com.cyy.transapp.model.RegisterState
import com.cyy.transapp.model.UsernameState
import com.cyy.transapp.view_model.UserViewModel
import com.cyy.transapp.view_model.UserViewModelFactory

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 使用ActivityResultLauncher进行意图跳转
        val resultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            // 意图结束后，执行这个「回调函数」
            ActivityResultCallback {
                if (it.resultCode == RESULT_OK) {
                    Toast.makeText(this, "退出登录成", Toast.LENGTH_LONG).show()
                }
            }
        )
        setContent {
            RegisterScreen(resultLauncher)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(resultLauncher: ActivityResultLauncher<Intent>) {
    val application = LocalContext.current.applicationContext as TransApp
    val userViewModel =
        viewModel<UserViewModel>(factory = UserViewModelFactory(application.userRepository))
    val username = userViewModel.username.collectAsState()
    val password = userViewModel.password.collectAsState()
    val confirmPassword = userViewModel.confirmPassword.collectAsState()
    val usernameState = userViewModel.usernameState.value
    val confirmPasswordState = userViewModel.confirmPasswordState.value
    val registerState = userViewModel.registerState
    val context = LocalContext.current as ComponentActivity

    registerState.observe(context) {
        if (registerState.value == RegisterState.SUCCESS) {
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra("username", username.value)
            resultLauncher.launch(intent)
            Toast.makeText(context, registerState.value!!.desc, Toast.LENGTH_LONG).show()
            registerState.value = RegisterState.NOT_BEGIN
        } else if (registerState.value == RegisterState.FAILED) {
            Toast.makeText(context, registerState.value!!.desc, Toast.LENGTH_LONG).show()
            registerState.value = RegisterState.NOT_BEGIN
        }
    }
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = username.value,
                onValueChange = userViewModel::updateUsername,
                label = { Text(text = "用户名") },
                isError = usernameState in listOf(
                    UsernameState.EXIST,
                    UsernameState.EMPTY
                ),
                trailingIcon = {
                    // 若已开始输入
                    if (usernameState != UsernameState.NOT_BEGIN) {
                        if (usernameState in listOf(UsernameState.EXIST, UsernameState.EMPTY)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.error),
                                contentDescription = null,
                                tint = Color.Red,
                                modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                            )
                        } else {
                            Icon(
                                painter = painterResource(id = R.drawable.correct),
                                contentDescription = null,
                                tint = Color(0xFF08A808)
                            )
                        }
                    }
                },
                colors = TextFieldDefaults.textFieldColors()
            )
            Text(text = usernameState.desc)
        }
        TextField(
            value = password.value,
            onValueChange = userViewModel::updatePassword,
            label = { Text(text = "密码") },
            colors = TextFieldDefaults.textFieldColors()
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = confirmPassword.value,
                onValueChange = userViewModel::updateConfirmPassword,
                label = { Text(text = "确认密码") },
                colors = TextFieldDefaults.textFieldColors(),
                trailingIcon = {
                    // 若已开始输入
                    if (confirmPasswordState != ConfirmPasswordState.NOT_BEGIN) {
                        if (confirmPasswordState == ConfirmPasswordState.DIFFERENT) {
                            Icon(
                                painter = painterResource(id = R.drawable.error),
                                contentDescription = null,
                                tint = Color.Red,
                                modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                            )
                        } else
                            Icon(
                                painter = painterResource(id = R.drawable.correct),
                                contentDescription = null,
                                tint = Color(0xFF08A808)
                            )
                    }
                }
            )
            Text(text = confirmPasswordState.desc)
        }
        Button(onClick = {
            userViewModel.register()
        }) {
            Text(text = "注册")
        }
    }
}

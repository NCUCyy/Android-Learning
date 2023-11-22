package com.cyy.exp2.psychological_test.ui

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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cyy.exp2.psychological_test.PsychologicalTestApp
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
            EnterScreen(resultLauncher)
        }
    }
}

/**
 * 使用livedata进行监视：若数据发生改变，则执行操作
 */
@OptIn(ExperimentalMaterial3Api::class)
// 登录界面
@Composable
fun EnterScreen(resultLauncher: ActivityResultLauncher<Intent>) {
    val context = LocalContext.current
    val application = LocalContext.current.applicationContext as PsychologicalTestApp
    val loginViewModel: LoginViewModel = viewModel()
    var username = loginViewModel.username.collectAsState()
    var password = loginViewModel.password.collectAsState()

    val userViewModel = viewModel<UserViewModel>(
        factory = UserViewModelFactory(
            application.userRepository,
        )
    )
    val loginUser = userViewModel.loginUser.collectAsState()
    // 监听登录状态！---使用LiveData而不是StateFlow
    userViewModel.loginRes.observe(context as ComponentActivity) {
        if (it) {
            Toast.makeText(application, "登录成功", Toast.LENGTH_SHORT).show()
            // 跳转到TestActivity(并携带userId，表示登录的用户)
            val intent = Intent(context as Activity, TestActivity::class.java)
            intent.putExtra("userId", loginUser.value?.id)
            resultLauncher.launch(intent)
        } else {
            Toast.makeText(application, "用户名或密码错误", Toast.LENGTH_SHORT).show()
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
                text = "心理测试 App",
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
                    contentColor = Color.White,
                ),
            ) {
                Box(
                    modifier = Modifier.padding(
                        top = 30.dp,
                        start = 20.dp,
                        end = 20.dp,
                        bottom = 20.dp
                    )
                ) {
                    InputBox(username, "请输入用户名...", action = loginViewModel::updateUsername)
                }
                Box(
                    modifier = Modifier.padding(
                        top = 5.dp,
                        start = 20.dp,
                        end = 20.dp,
                        bottom = 20.dp
                    )
                ) {
                    InputBox(password, "请输入密码...", action = loginViewModel::updatePassword)
                }
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
//                                userViewModel.register(username.value, password.value)
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

}


/**
 * 用到了状态提升！！！
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputBox(input: State<String>, placeHolder: String, action: (String) -> Unit) {
    Card(elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp)) {
        TextField(
            value = input.value,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp),
            onValueChange = {
                action(it)
            },
            placeholder = { Text(text = placeHolder) },
            singleLine = true, // 单行文本框
            shape = MaterialTheme.shapes.extraSmall, // 设置边框形状
            textStyle = TextStyle.Default.copy(color = Color.Black), // 设置文本颜色
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
        )
    }
}
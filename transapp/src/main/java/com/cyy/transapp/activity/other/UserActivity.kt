package com.cyy.transapp.activity.other

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cyy.transapp.TransApp
import com.cyy.transapp.view_model.user.UserConfigViewModel
import com.cyy.transapp.view_model.user.UserConfigViewModelFactory

/**
 * 用户信息活动
 */
class UserActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userId = intent.getIntExtra("userId", 0)
        setContent {
            UserScreen(userId)
        }
    }
}

@Composable
fun UserScreen(userId: Int) {
    val application = LocalContext.current.applicationContext as TransApp
    val userConfigViewModel = viewModel<UserConfigViewModel>(factory = UserConfigViewModelFactory(userId, application.userRepository))

}
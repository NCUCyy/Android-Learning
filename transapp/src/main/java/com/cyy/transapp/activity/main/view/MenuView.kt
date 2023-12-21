package com.cyy.transapp.activity.main.view

import android.app.Activity
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.cyy.transapp.activity.main.StateHolder
import kotlin.system.exitProcess

@Composable
fun MenuView(states: StateHolder) {
    val context = LocalContext.current as Activity
    DropdownMenu(expanded = states.dropState.value,
        onDismissRequest = {
            // 点击其他地方，则关闭下拉框
            states.dropState.value = false
        }) {
        DropdownMenuItem(
            // 在前面的Icon
            leadingIcon = {
                Icon(imageVector = Icons.Filled.Star, contentDescription = null)
            },
            text = {
                Text(text = "点赞App", fontSize = 20.sp)
            }, onClick = {
                // 点击完之后，关闭下拉框
                states.dropState.value = false
                Toast.makeText(context, "感谢支持！", Toast.LENGTH_LONG).show()
            })
        DropdownMenuItem(
            // 在前面的Icon
            leadingIcon = {
                Icon(imageVector = Icons.Filled.ExitToApp, contentDescription = null)
            },
            text = {
                Text(text = "退出App", fontSize = 20.sp)
            }, onClick = {
                // 点击完之后，关闭下拉框
                states.dropState.value = false
                ActivityCompat.finishAffinity(context)
                exitProcess(-1)
            })
    }
}
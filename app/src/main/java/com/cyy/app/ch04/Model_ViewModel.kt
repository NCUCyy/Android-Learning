package com.cyy.app.ch04

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyScreen(cyperViewModel: CyperViewModel = viewModel()) {
    val input = remember {
        mutableStateOf("")
    }
    val output = remember {
        mutableStateOf("")
    }
    val context = LocalContext.current as MainActivity
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text("加密解密应用", fontSize = 30.sp)
        TextField(value = input.value, onValueChange = {
            input.value = it
        })
        Row {
            Button(onClick = {
                cyperViewModel.encodeBase64(input.value)
                /**
                 * 只有当旋转屏幕（杀死这个context对于的Activity）时，才会触发页面output值的更新————确实能保留
                 */
                cyperViewModel.output.observe(context) {
                    output.value = it
                }
            }) {
                Text(text = "加密")
            }
            Button(onClick = {
                cyperViewModel.decodeBase64(input.value)
                cyperViewModel.output.observe(context) {
                    output.value = it
                }
            }) {
                Text(text = "解密")
            }
        }
        Text(text = "${cyperViewModel.output.value}")
    }
}
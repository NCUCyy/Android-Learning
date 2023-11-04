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
import androidx.compose.ui.unit.sp

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
fun MyScreen() {
    val input = remember {
        mutableStateOf("")
    }
    val output = remember {
        mutableStateOf("")
    }
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
                output.value = CyperViewModel().encodeBase64(input.value)
            }) {
                Text(text = "加密")
            }
            Button(onClick = {
                output.value = CyperViewModel().decodeBase64(input.value)
            }) {
                Text(text = "解密")
            }
        }
        if (output.value.isNotBlank())
            Text(text = output.value)
    }
}
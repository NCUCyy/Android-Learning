package com.cyy.app.ch04

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.gson.Gson

class Activity2 : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val id = intent.getIntExtra("id", 0)

        setContent {
            Screen1(id)
        }
    }
}

@Composable
fun Screen1(id: Int) {
    val context = LocalContext.current as Activity2
    val vm: TVM = viewModel()
    Log.i(vm.toString(),"123")
    Column {
        Button(onClick = {
            vm.update(id)
            context.finish()
        }) {
            Text("点击我")
        }
    }
}
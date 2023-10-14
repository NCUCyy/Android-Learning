package com.cyy.exp.ch02

import android.app.Activity
import android.content.Context
import android.os.Parcelable
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp

// 通用Screen
@Composable
fun <T : Parcelable> CommonScreen(message: T) {
    val context = LocalContext.current as Activity
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Yellow)
    ) {
        Text(text = "${message.toString()}", fontSize = 20.sp, maxLines = 2)
        Button(onClick = {
            context.setResult(Activity.RESULT_OK)
            context.finish()
        }) {
            Text(text = "返回")

        }
    }
}
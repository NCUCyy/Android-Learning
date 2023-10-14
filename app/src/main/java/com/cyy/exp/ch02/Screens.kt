package com.cyy.exp.ch02

import android.os.Parcelable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

// 通用Screen
@Composable
fun <T : Parcelable> CommonScreen(message: T) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "${message.toString()}", fontSize = 20.sp, maxLines = 2)
    }
}
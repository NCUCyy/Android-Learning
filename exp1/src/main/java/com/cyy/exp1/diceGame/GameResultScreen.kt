package com.cyy.exp1.diceGame

import android.content.ClipDescription
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.cyy.exp1.R

@Composable
fun GameResultScreen(imageId: Int, description: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Blue)
    ) {
        Row() {
            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "返回")
            Text("返回", fontSize = 30.sp, textAlign = TextAlign.Center)
        }
        Image(
            painter = painterResource(id = imageId),
            contentDescription = "$description",
            modifier = Modifier.fillMaxSize()
        )
    }
}

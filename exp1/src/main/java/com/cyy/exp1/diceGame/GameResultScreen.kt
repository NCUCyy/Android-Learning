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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.cyy.exp1.R

@Composable
fun GameResultScreen(imageId: Int, description: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Blue)
    ) {
        Image(
            painter = painterResource(id = imageId),
            contentDescription = "$description",
            modifier = Modifier.fillMaxSize()
        )
    }
}
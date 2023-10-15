package com.cyy.exp1.diceGame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.cyy.exp1.R

class GameLoseActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GameResultScreen(imageId = R.mipmap.lose, "输了")
        }
    }
}

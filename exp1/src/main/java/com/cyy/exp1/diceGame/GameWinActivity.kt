package com.cyy.exp1.diceGame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.cyy.exp1.R

class GameWinActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val result = intent.getStringExtra("result")
        setContent {
            GameResultScreen(imageId = R.mipmap.happy, result!!)
        }
    }
}

package com.cyy.transapp.activity.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable

class LearnActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userId = intent.getIntExtra("userId", 0)
        val vocabulary = intent.getStringExtra("vocabulary")!!
        setContent {
            LearnMainScreen(userId, vocabulary)
        }
    }
}

@Composable
fun LearnMainScreen(userId: Int, vocabulary: String) {

}
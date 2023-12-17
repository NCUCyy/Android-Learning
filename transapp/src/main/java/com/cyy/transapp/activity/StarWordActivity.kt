package com.cyy.transapp.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cyy.transapp.TransApp
import com.cyy.transapp.pojo.StarWord
import com.cyy.transapp.view_model.StarWordViewModel

class StarWordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userId = intent.getIntExtra("userId", 0)
        setContent {
            StarWordListScreen(userId)
        }
    }
}

@Composable
fun StarWordListScreen(userId: Int = 0) {
    val application = LocalContext.current.applicationContext as TransApp
    val starWordViewModel = StarWordViewModel(userId, application.starWordRepository)
    val starWords = starWordViewModel.starWords.collectAsStateWithLifecycle()
    LazyColumn {
        items(starWords.value) { starWord: StarWord ->
            StarWordCard(starWord)
        }
    }
}

@Composable
fun StarWordCard(starWord: StarWord) {
    Card(elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)) {
        Text(text = starWord.word)
    }
}
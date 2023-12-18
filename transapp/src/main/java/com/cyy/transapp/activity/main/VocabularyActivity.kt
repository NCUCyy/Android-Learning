package com.cyy.transapp.activity.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.cyy.transapp.TransApp
import com.cyy.transapp.model.Vocabulary
import com.cyy.transapp.view_model.VocabularyViewModel

class VocabularyActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VocabularyScreen()
        }
    }
}

@Composable
fun VocabularyScreen() {
    val application = LocalContext.current.applicationContext as TransApp
    val vocabularyViewModel = VocabularyViewModel(application.vocabularyRepository)
    LazyColumn {
        items(vocabularyViewModel.vocabularies) {
            VocabularyOptionCard(vocabulary = it)
        }
    }
}

@Composable
fun VocabularyOptionCard(vocabulary: Vocabulary) {
    val context = LocalContext.current as Activity
    Card(
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val intent = Intent()
                context.setResult(
                    Activity.RESULT_OK,
                    intent.putExtra("vocabulary", vocabulary)
                )
                context.finish()
            }
    ) {
        Text(text = vocabulary.desc)
    }
}

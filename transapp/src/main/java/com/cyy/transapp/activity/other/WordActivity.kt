package com.cyy.transapp.activity.other

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cyy.transapp.TransApp
import com.cyy.transapp.view_model.WordViewModel
import com.cyy.transapp.view_model.WordViewModelFactory

class WordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val vocabulary = intent.getStringExtra("vocabulary")!!
        setContent {
            WordScreen(vocabulary)
        }
    }
}

@Composable
fun WordScreen(vocabulary: String) {
    val context = LocalContext.current as Activity
    val application = LocalContext.current.applicationContext as TransApp
    val wordViewModel = viewModel<WordViewModel>(
        factory = WordViewModelFactory(
            application.vocabularyRepository,
            vocabulary,
            context
        )
    )
}
package com.cyy.transapp.activity.other

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.cyy.app.word_bank.model.WordItem
import com.cyy.transapp.R
import com.cyy.transapp.TransApp
import com.cyy.transapp.activity.main.view.toTransActivity
import com.cyy.transapp.model.OpResult
import com.cyy.transapp.view_model.WordViewModel
import com.cyy.transapp.view_model.WordViewModelFactory

class WordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userId = intent.getIntExtra("userId", 14)
        val vocabulary = intent.getStringExtra("vocabulary")!!
        val resultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            // 意图结束后，执行这个「回调函数」
            ActivityResultCallback {
            }
        )
        setContent {
            WordMainScreen(userId, vocabulary, resultLauncher)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordMainScreen(
    userId: Int,
    vocabulary: String,
    resultLauncher: ActivityResultLauncher<Intent>
) {
    val context = LocalContext.current as Activity
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = vocabulary,
                            textAlign = TextAlign.Center,
                            fontSize = (20 + syncFontSize.value).sp,
                        )
                    }
                },
                // 左侧图标
                navigationIcon = {
                    // 图标按钮
                    IconButton(onClick = {
                        // TODO：返回查词页面
                        context.finish()
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.back),
                            contentDescription = null,
                        )
                    }
                },
                actions = {
                }
            )
        },
        bottomBar = {
        },
        content = {
            Surface(color = bgColor.value) {
                Box(modifier = Modifier.fillMaxSize()) {
                    imageUri.value?.let {
                        Image(
                            painter = rememberAsyncImagePainter(model = imageUri.value),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.FillBounds
                        )
                    }
                }
                // 页面的主体部分
                Box(modifier = Modifier.padding(it)) {
                    // 侧滑导航视图（侧滑界面+导航图）
                    WordScreen(userId, vocabulary, resultLauncher)
                }
            }
        },
        floatingActionButton = {
        })
}

@Composable
fun WordScreen(userId: Int, vocabulary: String, resultLauncher: ActivityResultLauncher<Intent>) {
    val context = LocalContext.current as Activity
    val application = LocalContext.current.applicationContext as TransApp
    val wordViewModel = viewModel<WordViewModel>(
        factory = WordViewModelFactory(
            application.vocabularyRepository,
            vocabulary,
            context
        )
    )
    val loadVocabularyState = wordViewModel.loadVocabularyState.collectAsState()
    when (loadVocabularyState.value) {
        is OpResult.Success -> {
            WordListScreen(userId, wordViewModel, resultLauncher)
        }

        is OpResult.Loading -> {
            // TODO：显示加载中
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    // TODO：显示加载中
                    CircularProgressIndicator()
                }
        }

        else -> {
            // TODO：显示空页面
        }
    }
}

@Composable
fun WordListScreen(
    userId: Int,
    wordViewModel: WordViewModel,
    resultLauncher: ActivityResultLauncher<Intent>
) {
    val wordList = wordViewModel.wordList.collectAsState()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 10.dp, end = 10.dp)
    ) {
        items(wordList.value) { wordItem: WordItem ->
            WordItemCard(userId, wordItem, resultLauncher)
        }
    }
}

@Composable
fun WordItemCard(userId: Int, wordItem: WordItem, resultLauncher: ActivityResultLauncher<Intent>) {
    val context = LocalContext.current as Activity
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        modifier = Modifier
            .padding(top = 5.dp)
            .fillMaxWidth()
            .clickable {
                // 点击直接查询
                toTransActivity(context, resultLauncher, wordItem.word, userId)
            },
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = wordItem.word,
                modifier = Modifier.padding(20.dp),
                fontWeight = FontWeight.Bold,
                fontSize = (20 + syncFontSize.value).sp
            )
            val showTrans =
                "${wordItem.translations[0].type}. ${wordItem.translations[0].translation}"
            Text(
                text = showTrans,
                color = Color.Gray,
                fontSize = (15 + syncFontSize.value).sp,
            )
        }
    }
}

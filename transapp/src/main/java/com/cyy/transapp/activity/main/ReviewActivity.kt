package com.cyy.transapp.activity.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cyy.transapp.R
import com.cyy.transapp.TransApp
import com.cyy.transapp.activity.main.view.toTransActivity
import com.cyy.transapp.model.OpResult
import com.cyy.transapp.model.ReviewState
import com.cyy.transapp.view_model.learn_review.ReviewViewModel
import com.cyy.transapp.view_model.learn_review.ReviewViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ReviewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userId = intent.getIntExtra("userId", 14)
        val vocabulary = intent.getStringExtra("vocabulary")!!
//        val vocabulary = "CET6"
        // 使用ActivityResultLauncher进行意图跳转
        val resultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            // 意图结束后，执行这个「回调函数」
            ActivityResultCallback {
                if (it.resultCode == RESULT_OK && it.data?.hasExtra("record")!!) {
                    // nothing
                }
            }
        )
        setContent {
            // TODO：显示查询的词汇
            ReviewMainScreen(userId, vocabulary, resultLauncher)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewMainScreen(
    userId: Int,
    vocabulary: String,
    resultLauncher: ActivityResultLauncher<Intent>
) {
    val application = LocalContext.current.applicationContext as TransApp
    val context = LocalContext.current as Activity
    val reviewViewModel = viewModel<ReviewViewModel>(
        factory = ReviewViewModelFactory(
            userId,
            vocabulary,
            context,
            application.userRepository,
            application.todayRepository,
            application.planRepository,
            application.transRepository,
            application.vocabularyRepository,
            application.starWordRepository
        )
    )
    val starWord = reviewViewModel.starWord.value.collectAsStateWithLifecycle().value
    // 控制详细释义是否显示（过1s显示）
    val showDetail = remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // TODO：显示查询的词汇
//                    Text(text = query, fontWeight = FontWeight.Bold, fontSize = 25.sp, maxLines = 1)
                },
                // 左侧图标
                navigationIcon = {
                    // 图标按钮
                    IconButton(onClick = {
                        // TODO：返回查词页面
                        reviewViewModel.endReview()
                        context.finish()
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.back),
                            contentDescription = null,
                        )
                    }
                },
                actions = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (starWord != null) {
                            StarIconButton(
                                action = {
                                    // TODO：取消收藏
                                    reviewViewModel.unstarWord()
                                    Toast.makeText(context, "取消收藏成功！", Toast.LENGTH_SHORT)
                                        .show()
                                },
                                icon = R.drawable.star_fill
                            )
                        } else {
                            StarIconButton(
                                action = {
                                    // TODO：收藏
                                    reviewViewModel.starWord()
                                    Toast.makeText(context, "收藏成功！", Toast.LENGTH_SHORT).show()
                                },
                                icon = R.drawable.star
                            )
                        }
                        IconButton(onClick = {
                            // TODO：移除
                            reviewViewModel.removeWord()
                            Toast.makeText(context, "移除成功！", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.delete),
                                contentDescription = null,
                                Modifier.size(30.dp),
                                tint = Color(0xFFE90C57)
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (showDetail.value) {
                TranslateOrNextBtn(
                    reviewViewModel = reviewViewModel,
                    resultLauncher = resultLauncher,
                    showDetail = showDetail
                )
            } else {
                ReviewBtnGroup(reviewViewModel, showDetail)
            }
        },
        content = {
            // 页面的主体部分
            Box(modifier = Modifier.padding(it)) {
                ReviewContentScreen(reviewViewModel, showDetail)
            }
        },
        floatingActionButton = {
        })
}

@Composable
fun ReviewBtnGroup(reviewViewModel: ReviewViewModel, showDetail: MutableState<Boolean>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ReviewBtn(ReviewState.Known, reviewViewModel::setKnown, showDetail)
        ReviewBtn(ReviewState.Ambitious, reviewViewModel::setAmbitious, showDetail)
        ReviewBtn(ReviewState.Unknown, reviewViewModel::setUnknown, showDetail)
    }
}

@Composable
fun ReviewBtn(
    reviewState: ReviewState,
    action: () -> Unit = {},
    showDetail: MutableState<Boolean>
) {
    Card(
        modifier = Modifier
            .clickable {
                action.invoke()
                showDetail.value = true
            },
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp)
    ) {
        Text(
            text = reviewState.desc,
            modifier = Modifier.padding(10.dp),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ReviewContentScreen(
    reviewViewModel: ReviewViewModel,
    showDetail: MutableState<Boolean>
) {
    val loadVocabularyState = reviewViewModel.loadVocabularyState.collectAsState()

    Column(modifier = Modifier.padding(10.dp)) {
        when (loadVocabularyState.value) {
            is OpResult.Success -> {
                // 当前Word
                TitleWordCard(reviewViewModel)
                Spacer(modifier = Modifier.height(20.dp))
                if (showDetail.value) {
                    // 若选择完成，则显示完整释义（点击进行联网搜索）
                    DetailWordCard(reviewViewModel)
                }
            }

            is OpResult.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is OpResult.NotBegin -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "恭喜你完成全部复习任务！")
                }
                // 延迟1s后退出
                val scope = rememberCoroutineScope()
                val context = LocalContext.current as Activity
                scope.launch {
                    delay(1000)
                    context.finish()
                }
            }

            else -> {

            }
        }
    }
}

@Composable
fun TranslateOrNextBtn(
    reviewViewModel: ReviewViewModel,
    resultLauncher: ActivityResultLauncher<Intent>,
    showDetail: MutableState<Boolean>
) {
    val context = LocalContext.current as Activity
    val curQuizWord = reviewViewModel.curQuizWord.collectAsState().value
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Card(
            modifier = Modifier
                .clickable {
                    // TODO：联网搜索
                    toTransActivity(
                        context,
                        resultLauncher,
                        curQuizWord.word,
                        reviewViewModel.userId
                    )
                },
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp)
        ) {
            Text(
                text = "查看完整释义",
                modifier = Modifier.padding(10.dp),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Card(
            modifier = Modifier
                .clickable {
                    // TODO：下一个
                    reviewViewModel.nextWord()
                    showDetail.value = false
                },
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp)
        ) {
            Text(
                text = "下一个",
                modifier = Modifier.padding(10.dp),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun DetailWordCard(reviewViewModel: ReviewViewModel) {
    val curWordItem = reviewViewModel.curWordItem.collectAsState().value
    val phrases = curWordItem.phrases
    val translations = curWordItem.translations
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .padding(start = 10.dp, top = 10.dp)
            .verticalScroll(scrollState)
    ) {
        if (translations.isNotEmpty()) {
            Text(
                text = "释义",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
            )
            TitleBodyDivider()
            TranslationCard(translations)
        }
        Spacer(modifier = Modifier.height(20.dp))
        if (phrases.isNotEmpty()) {
            Text(
                text = "固定搭配",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
            )
            TitleBodyDivider()
            PhraseCard(phrases)
        }
    }
}


@Composable
fun TitleWordCard(reviewViewModel: ReviewViewModel) {
    val curQuizWord = reviewViewModel.curQuizWord.collectAsState().value
    val curProcess = reviewViewModel.curWordProcess.collectAsState().value
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = curQuizWord.word,
            fontWeight = FontWeight.Bold,
            fontSize = 40.sp,
            modifier = Modifier.padding(start = 10.dp)
        )
        // TODO
        ProcessCard(curProcess)
    }
}
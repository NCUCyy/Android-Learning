package com.cyy.transapp.activity.main

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cyy.transapp.R
import com.cyy.transapp.TransApp
import com.cyy.transapp.model.OpResult
import com.cyy.transapp.view_model.LearnViewModel
import com.cyy.transapp.view_model.LearnViewModelFactory
import kotlin.concurrent.thread

class LearnActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userId = intent.getIntExtra("userId", 0)
        val vocabulary = intent.getStringExtra("vocabulary")!!
        setContent {

            // TODO：显示查询的词汇
            LearnMainScreen(userId, vocabulary)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnMainScreen(userId: Int, vocabulary: String) {
    val application = LocalContext.current.applicationContext as TransApp
    val context = LocalContext.current as Activity
    val learnViewModel = viewModel<LearnViewModel>(
        factory = LearnViewModelFactory(
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
    val isCurStared = learnViewModel.isCurStared.collectAsState()
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
                        learnViewModel.endLearn()
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
                        if (isCurStared.value) {
                            StarIconButton(
                                action = {
                                    // TODO：取消收藏
                                    learnViewModel.unstarWord()
                                    Toast.makeText(context, "取消收藏成功！", Toast.LENGTH_SHORT)
                                        .show()
                                },
                                icon = R.drawable.star_fill
                            )
                        } else {
                            StarIconButton(
                                action = {
                                    // TODO：收藏
                                    learnViewModel.starWord()
                                    Toast.makeText(context, "收藏成功！", Toast.LENGTH_SHORT).show()
                                },
                                icon = R.drawable.star
                            )
                        }
                        IconButton(onClick = {
                            // TODO：移除
                            learnViewModel.removeWord()
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
        content = {
            // 页面的主体部分
            Box(modifier = Modifier.padding(it)) {
                LearnContentScreen(learnViewModel)
            }
        },
        floatingActionButton = {
        })
}

@Composable
fun StarIconButton(action: () -> Unit, icon: Int) {
    IconButton(onClick = {
        action.invoke()
    }, modifier = Modifier.padding(end = 16.dp)) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            Modifier.size(30.dp),
            tint = Color(0xFFE8C11C)
        )
    }
}

@Composable
fun LearnContentScreen(learnViewModel: LearnViewModel) {
    val loadVocabularyState = learnViewModel.loadVocabularyState.collectAsState()
    Column(modifier = Modifier.padding(10.dp)) {

        when (loadVocabularyState.value) {
            is OpResult.Success -> {
                QuizWordCard(learnViewModel)
                Button(onClick = { learnViewModel.nextWord() }) {
                    Text(text = "下一个")
                }
                Text(text = learnViewModel.curPlanWord.collectAsState().value.process.toString())
            }

            is OpResult.Loading -> {
                CircularProgressIndicator(modifier = Modifier.fillMaxSize())
            }

            else -> {

            }
        }
    }
}

@Composable
fun QuizWordCard(learnViewModel: LearnViewModel) {
    // TODO：显示查询的词汇
    val curQuizWord = learnViewModel.curQuizWord.collectAsState().value
    val curPlanWord = learnViewModel.curPlanWord.collectAsState().value
    val curProcess = learnViewModel.curWordProcess.collectAsState().value
    Column(modifier = Modifier.padding(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = curQuizWord.word,
                fontWeight = FontWeight.Bold,
                fontSize = 40.sp,
                modifier = Modifier.padding(10.dp)
            )
            Text(
                text = curProcess.toString(),
                fontWeight = FontWeight.Bold,
                fontSize = 40.sp,
                modifier = Modifier.padding(10.dp)
            )
        }
        curQuizWord.options.forEach { option: String ->
            OptionCard(curQuizWord.word, option, curQuizWord.answer, learnViewModel)
        }
    }
}

@Composable
fun OptionCard(word: String, option: String, answer: String, learnViewModel: LearnViewModel) {
    val scope = rememberCoroutineScope()
    val containColorState = remember { mutableStateOf(Color.White) }
    val contentColoState = remember { mutableStateOf(Color.Black) }

    val curOption = learnViewModel.curOption.collectAsState().value

    LaunchedEffect(curOption) {
        containColorState.value = Color.White
        contentColoState.value = Color.Black
        if (curOption != "") {
            // 若当前选项为正确答案，则显示绿色
            if (answer == curOption) {
                if (option == curOption) {
                    // 正确
                    containColorState.value = Color(0xFF98FB98)
                    contentColoState.value = Color.Black
                }
            } else {
                if (option == answer) {
                    containColorState.value = Color(0xFF98FB98)
                    contentColoState.value = Color.Black
                }
                if (option == curOption) {
                    containColorState.value = Color(0xFFE90C57)
                    contentColoState.value = Color.White
                }
            }
        }
    }
    Card(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()
            .clickable {
                if (curOption == "")
                    learnViewModel.setCurOption(option)
                // 过两秒自动跳转到下一题
                thread {
                    Thread.sleep(1000)
                    learnViewModel.nextWord()
                }
            },
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
        colors = CardDefaults.cardColors(
            containerColor = containColorState.value,
            contentColor = contentColoState.value
        )
    ) {
        val type = option.split(". ")[0]
        val translation = option.split(". ")[1]
        Text(
            text = "$type. ",
            modifier = Modifier.padding(start = 15.dp, top = 10.dp, bottom = 5.dp),
            color = Color.Gray,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = translation,
            fontSize = 17.sp,
            modifier = Modifier.padding(start = 15.dp, bottom = 10.dp)
        )
    }
}

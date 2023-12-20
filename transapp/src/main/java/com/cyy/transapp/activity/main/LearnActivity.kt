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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cyy.app.word_bank.model.Phrase
import com.cyy.app.word_bank.model.Translation
import com.cyy.transapp.R
import com.cyy.transapp.TransApp
import com.cyy.transapp.model.OpResult
import com.cyy.transapp.view_model.LearnViewModel
import com.cyy.transapp.view_model.LearnViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LearnActivity : ComponentActivity() {
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
            LearnMainScreen(userId, vocabulary, resultLauncher)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnMainScreen(
    userId: Int,
    vocabulary: String,
    resultLauncher: ActivityResultLauncher<Intent>
) {
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
    val starWord = learnViewModel.starWord.value.collectAsStateWithLifecycle().value
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
                        if (starWord != null) {
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
            if (showDetail.value)
                TranslateOrNextBtn(
                    learnViewModel = learnViewModel,
                    resultLauncher = resultLauncher,
                    showDetail = showDetail
                )
        },
        content = {
            // 页面的主体部分
            Box(modifier = Modifier.padding(it)) {
                LearnContentScreen(learnViewModel, resultLauncher, showDetail)
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
fun LearnContentScreen(
    learnViewModel: LearnViewModel,
    resultLauncher: ActivityResultLauncher<Intent>,
    showDetail: MutableState<Boolean>
) {
    val loadVocabularyState = learnViewModel.loadVocabularyState.collectAsState()
    val curOption = learnViewModel.curOption.collectAsState().value

    Column(modifier = Modifier.padding(10.dp)) {
        when (loadVocabularyState.value) {
            is OpResult.Success -> {
                // 当前Word
                TitleWordCard(learnViewModel)
                Spacer(modifier = Modifier.height(20.dp))
                if (showDetail.value) {
                    // 若选择完成，则显示完整释义（点击进行联网搜索）
                    DetailWordCard(learnViewModel)
                } else {
                    // 四个选项
                    OptionWordCard(learnViewModel, showDetail)
                }
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
fun TranslateOrNextBtn(
    learnViewModel: LearnViewModel,
    resultLauncher: ActivityResultLauncher<Intent>,
    showDetail: MutableState<Boolean>
) {
    val context = LocalContext.current as Activity
    val curQuizWord = learnViewModel.curQuizWord.collectAsState().value
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Card(
            modifier = Modifier
                .clickable {
                    // TODO：联网搜索
                    val intent = Intent(context, TransActivity::class.java)
                    intent.putExtra("userId", learnViewModel.userId)
                    intent.putExtra("query", curQuizWord.word)
                    resultLauncher.launch(intent)
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
                    learnViewModel.nextWord()
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
fun DetailWordCard(learnViewModel: LearnViewModel) {
    val curWordItem = learnViewModel.curWordItem.collectAsState().value
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
            TitleDivider()
            TranslationCard(translations)
        }
        Spacer(modifier = Modifier.height(20.dp))
        if (phrases.isNotEmpty()) {
            Text(
                text = "固定搭配",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
            )
            TitleDivider()
            PhraseCard(phrases)
        }
    }
}

@Composable
fun TitleDivider() {
    Divider(
        modifier = Modifier
            .fillMaxWidth(),
        color = Color(0xFFDAD9D9),
    )
}

@Composable
fun TranslationCard(translation: List<Translation>) {
    Column(modifier = Modifier.padding(start = 40.dp)) {
        translation.forEach { translation: Translation ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(5.dp)
                ) {
                    Text(
                        text = "${translation.type}.",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(
                            start = 5.dp,
                            end = 5.dp,
                            bottom = 2.dp
                        ),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontStyle = FontStyle.Italic
                        )
                    )
                }
                Spacer(modifier = Modifier.width(20.dp))
                Text(
                    text = translation.translation,
                    modifier = Modifier.padding(top = 5.dp, bottom = 8.dp)
                )
            }
        }
    }
}

@Composable
fun PhraseCard(phrases: List<Phrase>) {
    Column(modifier = Modifier.padding(start = 40.dp)) {
        phrases.forEach { phrase: Phrase ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(5.dp)
            ) {
                Text(
                    text = phrase.phrase,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(
                        start = 5.dp,
                        end = 5.dp,
                        bottom = 2.dp
                    ),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontStyle = FontStyle.Italic
                    )
                )
            }
            Text(text = phrase.translation, modifier = Modifier.padding(top = 5.dp, bottom = 8.dp))
        }
    }
}

@Composable
fun TitleWordCard(learnViewModel: LearnViewModel) {
    val curQuizWord = learnViewModel.curQuizWord.collectAsState().value
    val curProcess = learnViewModel.curWordProcess.collectAsState().value
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

@Composable
fun ProcessCard(curProcess: Int) {
    // TODO：显示当前进度————球球
    Row(modifier = Modifier.padding(start = 12.dp, top = 5.dp)) {
        when (curProcess) {
            0 -> {
                ProcessIcon(0)
                ProcessIcon(0)
                ProcessIcon(0)
            }

            1 -> {
                ProcessIcon(1)
                ProcessIcon(0)
                ProcessIcon(0)
            }

            2 -> {
                ProcessIcon(1)
                ProcessIcon(2)
                ProcessIcon(0)
            }

            3 -> {
                ProcessIcon(1)
                ProcessIcon(2)
                ProcessIcon(3)
            }
        }
    }
}

@Composable
fun ProcessIcon(process: Int) {
    val tintColor = Color(0xFF4CAF50)
    when (process) {
        0 -> Icon(
            painter = painterResource(id = R.drawable.counter_0),
            contentDescription = null,
            modifier = Modifier.size(30.dp),
            tint = Color(0xFFE90C57)
        )

        1 -> Icon(
            painter = painterResource(id = R.drawable.counter_1),
            contentDescription = null,
            modifier = Modifier.size(30.dp),
            tint = tintColor
        )

        2 -> Icon(
            painter = painterResource(id = R.drawable.counter_2),
            contentDescription = null,
            modifier = Modifier.size(30.dp),
            tint = tintColor
        )

        3 -> Icon(
            painter = painterResource(id = R.drawable.counter_3),
            contentDescription = null,
            modifier = Modifier.size(30.dp),
            tint = tintColor
        )
    }
}

@Composable
fun OptionWordCard(learnViewModel: LearnViewModel, showDetail: MutableState<Boolean>) {
    // TODO：显示查询的词汇
    val curQuizWord = learnViewModel.curQuizWord.collectAsState().value
    Column(modifier = Modifier.padding(10.dp)) {
        curQuizWord.options.forEach { option: String ->
            OptionCard(curQuizWord.word, option, curQuizWord.answer, learnViewModel, showDetail)
        }
    }
}

@Composable
fun OptionCard(
    word: String,
    option: String,
    answer: String,
    learnViewModel: LearnViewModel,
    showDetail: MutableState<Boolean>
) {
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
                if (curOption == "") {// 只能选择一次
                    learnViewModel.setCurOption(option)
                    // 过一秒自动显示详细释义
                    scope.launch {
                        delay(1000)
                        showDetail.value = true
                    }
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

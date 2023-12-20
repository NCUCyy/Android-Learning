package com.cyy.transapp.activity.main

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cyy.transapp.R
import com.cyy.transapp.TransApp
import com.cyy.transapp.activity.other.VocabularyActivity
import com.cyy.transapp.activity.other.WordActivity
import com.cyy.transapp.model.LearnProcess
import com.cyy.transapp.model.OpResult
import com.cyy.transapp.model.daily_sentence.SentenceModel
import com.cyy.transapp.pojo.ListenResource
import com.cyy.transapp.pojo.TransRecord
import com.cyy.transapp.util.FileUtil
import com.cyy.transapp.view_model.LearnReviewViewModel
import com.cyy.transapp.view_model.ListenViewModel
import com.cyy.transapp.view_model.ListenViewModelFactory
import com.cyy.transapp.view_model.QueryViewModel
import com.google.gson.Gson

val screens = listOf(Screen.QueryPage, Screen.ListenPage, Screen.LearnPage)

/**
 *Screen类（与用于显示的Screen实体不同！要区分开！Screen类只用于提供页面需要的元数据metaData：icon、title、"route"【用于导航】）
 */
sealed class Screen(val route: String, val title: String, val icon: Int) {
    object QueryPage :
        Screen(route = "query", title = "查词", icon = R.drawable.trans)

    object ListenPage :
        Screen(route = "listen", title = "听力", icon = R.drawable.listen)

    object LearnPage :
        Screen(route = "learn", title = "学习", icon = R.drawable.dictionary)
}

// ----------------------------------------------------①QueryScreen----------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueryScreen(states: StateHolder, queryViewModel: QueryViewModel) {
    val context = LocalContext.current as Activity
    val query = queryViewModel.query.collectAsState()
    val sentenceState = queryViewModel.sentenceState.collectAsState()
    val transRecords = queryViewModel.transRecords.collectAsState()
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        // 1、输入框
        TextField(
            value = query.value,
            modifier = Modifier
                .fillMaxWidth(),
            onValueChange = { it: String ->
                queryViewModel.updateQuery(it)
            },
            placeholder = {
                Text(text = "查询单词或句子")
            },
            shape = MaterialTheme.shapes.extraSmall, // 设置边框形状
            textStyle = TextStyle.Default.copy(color = Color.Black, fontSize = 16.sp), // 设置文本颜色
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent,
//                focusedIndicatorColor = Color.DarkGray,
//                unfocusedIndicatorColor = Color.DarkGray,
                disabledIndicatorColor = Color.Transparent,
                placeholderColor = Color.Gray,
            ), keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(onSearch = {
                // TODO:跳转到TransActivity
                toTransActivity(context, states, query.value, queryViewModel.userId)
            })
        )
        Spacer(modifier = Modifier.height(10.dp))
        // 2、每日一句
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .size(width = Dp.Infinity, height = 180.dp)
        ) {
            when (sentenceState.value) {
                is OpResult.Success -> {
                    DailySentenceCard(
                        (sentenceState.value as OpResult.Success<*>).data as SentenceModel,
                        states,
                        queryViewModel.userId
                    )
                }

                is OpResult.Error -> {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Button(
                            onClick = {
                                queryViewModel.requestSentence()
                            }, colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = Color.Gray,
                            )
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.network_error),
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = (sentenceState.value as OpResult.Error<Any>).errorDesc.toString(),
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Center,
                                )

                            }
                        }

                    }
                }

                is OpResult.Loading -> {
                    // TODO：显示加载中
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator()
                    }
                }

                else -> {
                    // TODO：显示空页面
                }
            }
        }
        Spacer(modifier = Modifier.height(15.dp))
        // 3、查词历史
        Column(modifier = Modifier.verticalScroll(scrollState)) {
            transRecords.value.forEachIndexed { idx: Int, transRecord: TransRecord ->
                TransRecordCard(
                    transRecord = transRecord,
                    isLast = idx == transRecords.value.size - 1,
                    states = states,
                    userId = queryViewModel.userId
                )
            }
        }
    }
}

@Composable
fun DailySentenceCard(sentenceModel: SentenceModel, states: StateHolder, userId: Int) {
    val context = LocalContext.current as Activity
    val data = sentenceModel.data
    Card(
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
        shape = RoundedCornerShape(5.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFEBF4FA),
            contentColor = Color.Black
        ), modifier = Modifier.clickable {
            toTransActivity(context, states, data.en, userId)
        }
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = "${data.day}.${data.month}",
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                color = Color(0xFF2196F3),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(10.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = data.en,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 10.dp, end = 10.dp)
            )
            Text(
                text = data.zh,
                fontSize = 15.sp,
                color = Color.Gray,
                modifier = Modifier.padding(10.dp)
            )
        }

    }
}

@Composable
fun TransRecordCard(transRecord: TransRecord, isLast: Boolean, states: StateHolder, userId: Int) {
    val context = LocalContext.current as Activity
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                // 点击卡片即可翻译查询
                toTransActivity(context, states, transRecord.word, userId)
            }, colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        ConstraintLayout {
            val (iconRef, wordRef, transRef, freqRef) = createRefs()
            val vGuideline1 = createGuidelineFromStart(35.dp)
            val hGuideline = createGuidelineFromTop(0.5f)
            val vGuideline2 = createGuidelineFromStart(300.dp)
            val maxWidth = 320.dp
            Icon(painter = painterResource(id = R.drawable.history),
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.constrainAs(
                    iconRef
                ) {
                    start.linkTo(parent.start)
                    end.linkTo(vGuideline1)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                })
            Text(
                text = transRecord.word,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(start = 10.dp)
                    // 注意：为了让word完整显示出来，需要手动控制Text的size（主要是width，height设置为充满给定的高度即可）
                    .size(height = Dp.Infinity, width = maxWidth)
                    .constrainAs(wordRef) {
                        start.linkTo(vGuideline1)
                        top.linkTo(parent.top)
                        bottom.linkTo(hGuideline)
                    }
            )
            Text(
                text = transRecord.trans,
                fontSize = 15.sp,
                color = Color.Gray,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .size(height = Dp.Infinity, width = maxWidth)
                    .constrainAs(transRef) {
                        start.linkTo(vGuideline1)
                        top.linkTo(hGuideline)
                        bottom.linkTo(parent.bottom)
                    })
            if (transRecord.freq > 1)
                Card(
                    shape = RoundedCornerShape(5.dp),
                    modifier = Modifier.constrainAs(freqRef) {
                        start.linkTo(vGuideline2)
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }) {
                    Text(
                        text = "已查 ${transRecord.freq} 次",
                        fontSize = 10.sp,
                        color = Color.Gray,
                        modifier = Modifier
                            .padding(5.dp)
                    )
                }
        }
    }
    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        color = Color(0xFFDAD9D9),
    )
    if (isLast) {
        Text(
            text = "我是有底线的哟~",
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            fontSize = 15.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * 跳转到TransActivity---进行翻译
 */
fun toTransActivity(context: Activity, states: StateHolder, query: String, userId: Int) {
    val intent = Intent(context, TransActivity::class.java)
    intent.putExtra("query", query)
    intent.putExtra("userId", userId)
    states.resultLauncher.launch(intent)
}

// ----------------------------------------------------②ListenScreen----------------------------------------------------
@Composable
fun ListenScreen(states: StateHolder) {
    val application = LocalContext.current.applicationContext as TransApp
    val listenViewModel =
        viewModel<ListenViewModel>(factory = ListenViewModelFactory(application.listenRepository))
    val listenResources = listenViewModel.getALlListenResource()
    LazyColumn {
        items(listenResources) { listenResource ->
            ListenResourceCard(listenResource, states)
        }
    }
}

@Composable
fun ListenResourceCard(listenResource: ListenResource, states: StateHolder) {
    val context = LocalContext.current as Activity
    // 从raw读取txt
    val en = FileUtil.readRawToTxt(context, listenResource.en)
    val zh = FileUtil.readRawToTxt(context, listenResource.zh)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable {
                // TODO：跳转到ListenActivity
                val intent = Intent(context, ListenActivity::class.java)
                intent.putExtra("listenResource", listenResource)
                states.resultLauncher.launch(intent)
            },
        shape = RoundedCornerShape(5.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFEBF4FA),
            contentColor = Color.Black
        ), elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp)
    ) {
        Row {
            Image(
                painter = painterResource(id = listenResource.img),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .padding(start = 10.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp)
            ) {
                Text(
                    text = listenResource.topic,
                    fontSize = 23.sp,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF2196F3),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(10.dp)
                )
                Text(
                    text = en,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                    maxLines = 2,
                    color = Color.Gray
                )
            }
        }
    }
}

// ----------------------------------------------------③LearnScreen----------------------------------------------------
/**
 * 显示当前词汇表的总词数：获得整个词汇表
 * learnProcess：
 */
@Composable
fun LearnScreen(states: StateHolder, learnReviewViewModel: LearnReviewViewModel) {
    val scrollState = rememberScrollState()
    val loadVocabularyState = learnReviewViewModel.loadVocabularyState.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        when (loadVocabularyState.value) {
            is OpResult.Success, OpResult.NotBegin -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    VocabularyCard(states, learnReviewViewModel)
                    Spacer(modifier = Modifier.width(10.dp))
                    ProgressCard(states, learnReviewViewModel)
                }
                DailyAttendanceCard(states, learnReviewViewModel)
                TodayCard(states, learnReviewViewModel)
                LearnAndReviewCard(states, learnReviewViewModel)
            }


            is OpResult.Loading -> {
                // TODO：显示加载中
                CircularProgressIndicator(modifier = Modifier.fillMaxSize())
            }

            else -> {
                // TODO：显示空页面
            }
        }

    }
}

@Composable
fun VocabularyCard(states: StateHolder, learnReviewViewModel: LearnReviewViewModel) {
    val context = LocalContext.current as Activity
    val curUser = learnReviewViewModel.curUser.collectAsStateWithLifecycle()
    val vocabularySize = learnReviewViewModel.vocabulary.collectAsState().value.size
    Card(
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
        modifier = Modifier
            .clickable {
                if (curUser.value.vocabulary == "未选择") {
                    // TODO：跳转到VocabularyActivity(选择字典)
                    val intent = Intent(context, VocabularyActivity::class.java)
                    states.resultLauncher.launch(intent)
                } else {
                    // TODO：跳转到WordActivity(选择字典)
                    val intent = Intent(context, WordActivity::class.java)
                    states.resultLauncher.launch(intent)
                }

            }
            .size(width = 180.dp, height = 150.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        )
    ) {
        Row {
            Text(
                text = "Vocabulary",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(10.dp)
            )
            Spacer(modifier = Modifier.width(15.dp))
            Icon(
                painter = painterResource(id = R.drawable.navigate_next),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .padding(top = 10.dp)
            )
        }
        Text(
            text = curUser.value.vocabulary,
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            color = Color(0xFF269C2A)
        )
        Text(
            text = "共计 $vocabularySize 个单词",
            modifier = Modifier.padding(start = 10.dp),
            color = Color.Gray,
            fontSize = 13.sp
        )
    }
}

@Composable
fun ProgressCard(states: StateHolder, learnReviewViewModel: LearnReviewViewModel) {
    val vocabularySize = learnReviewViewModel.vocabulary.collectAsState().value.size
    val plan = learnReviewViewModel.plan.value.collectAsStateWithLifecycle()
    Card(
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
        modifier = Modifier.size(width = 180.dp, height = 150.dp), colors = CardDefaults.cardColors(
            containerColor = Color.White,
        )
    ) {
        Row {
            Text(
                text = "Progress",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(10.dp)
            )
            Spacer(modifier = Modifier.width(35.dp))
            Icon(
                painter = painterResource(id = R.drawable.progress),
                contentDescription = null,
                modifier = Modifier.padding(top = 15.dp)
            )
        }
        if (plan.value.vocabulary != "") {
            // 查询好了
            if (plan.value.vocabulary != "未选择") {
                // 有选择
                val learnProcess =
                    Gson().fromJson(plan.value.learnProcess, LearnProcess::class.java)
                val process =
                    "%.1f".format(learnProcess.learnedNum * 100 / vocabularySize.toFloat())
                Text(
                    text = "$process %",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    color = Color(0xFF269C2A)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "已掌握 ${learnProcess.learnedNum} 个单词",
                    modifier = Modifier.padding(start = 10.dp),
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            } else {
                // 没选择
                Text(
                    text = "0.0 %",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    color = Color(0xFF269C2A)
                )
            }
        }
    }
}

@Composable
fun LearnAndReviewCard(states: StateHolder, learnReviewViewModel: LearnReviewViewModel) {
    val context = LocalContext.current as Activity
    val plan = learnReviewViewModel.plan.value.collectAsStateWithLifecycle()
    val curUser = learnReviewViewModel.curUser.collectAsStateWithLifecycle()
    Row(verticalAlignment = Alignment.CenterVertically) {
        Button(onClick = {
            if (plan.value.vocabulary != "") {
                if (plan.value.vocabulary != "未选择") {
                    // 已经查询结束，并且不是"未选择"
                    val learnProcess = learnReviewViewModel.getLearnProcess(plan.value)
                    val reviewProcess = learnReviewViewModel.getReviewProcess(plan.value)
                    if (reviewProcess.process.size > 0) {
                        // TODO：若还有没复习的，询问是否要先复习
                        if (learnProcess.process.size > 0) {
                            // TODO：跳转到LearnActivity
                            val intent = Intent(context, LearnActivity::class.java)
                            intent.putExtra("userId", curUser.value.id)
                            intent.putExtra("vocabulary", plan.value.vocabulary)
                            states.resultLauncher.launch(intent)
                        } else {
                            // TODO：开启下一组学习（弹窗确认）
                            learnReviewViewModel.initLearnProcess()
                        }
                    }
                } else {
                    toVocabularyActivity(context, states)
                }
            }
        }, shape = RoundedCornerShape(5.dp)) {
            if (plan.value.vocabulary != "") {
                // 查询好了
                if (plan.value.vocabulary != "未选择") {
                    // 有选择
                    val learnProcess = learnReviewViewModel.getLearnProcess(plan.value)
                    if (learnProcess.process.size > 0)
                        Text(text = "Learn:${learnProcess.process.size}")
                    else
                        Text(text = "学习下一组")
                } else {
                    // 无选择
                    Text(text = "请先选择词库")
                }
            }
        }
        Button(onClick = {
            // TODO：跳转到ReviewActivity
            if (plan.value.vocabulary != "") {
                if (plan.value.vocabulary != "未选择") {
                    val intent = Intent(context, ReviewActivity::class.java)
                    intent.putExtra("userId", curUser.value.id)
                    intent.putExtra("vocabulary", curUser.value.vocabulary)
                    states.resultLauncher.launch(intent)
                } else {
                    // 跳转到VocabularyActivity
                    toVocabularyActivity(context, states)
                }
            }
        }) {
            if (plan.value.vocabulary != "") {
                // 查询好了
                if (plan.value.vocabulary != "未选择") {
                    // 有选择
                    val reviewProcess = learnReviewViewModel.getReviewProcess(plan.value)
                    Text(text = "Review:${reviewProcess.process.size}")
                } else {
                    // 无选择
                    Text(text = "请先选择词库")
                }
            }
        }
    }
}

fun toVocabularyActivity(context: Activity, states: StateHolder) {
    val intent = Intent(context, VocabularyActivity::class.java)
    states.resultLauncher.launch(intent)
}


@Composable
fun DailyAttendanceCard(states: StateHolder, learnReviewViewModel: LearnReviewViewModel) {
    val todays = learnReviewViewModel.todays.collectAsStateWithLifecycle()
    LazyRow {
        items(todays.value) {
            Card {
                Text(text = it.day.toString())
            }
        }
    }
}

@Composable
fun TodayCard(states: StateHolder, learnReviewViewModel: LearnReviewViewModel) {
    val today = learnReviewViewModel.today.collectAsStateWithLifecycle().value
    Card(
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        )
    ) {
        Text(
            text = "Today",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(10.dp)
        )
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TodayCardItem(
                "复习单词",
                today.reviewNum.toString(),
                R.drawable.book,
                iconTint = Color(0xFF476D1A),
                containerColor = Color(0xFFE7BAAC)
            )
            Spacer(modifier = Modifier.width(10.dp))
            TodayCardItem(
                "新学单词",
                today.newLearnNum.toString(),
                R.drawable.book,
                iconTint = Color(0xFF476D1A),
                containerColor = Color(0xFFE7BAAC)
            )
        }
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TodayCardItem(
                "收藏单词",
                today.starNum.toString(),
                R.drawable.star2,
                iconTint = Color(0xFFDDB405),
                containerColor = Color(0xFFF8EF9D)
            )
            Spacer(modifier = Modifier.width(10.dp))
            TodayCardItem(
                "移除单词",
                today.removeNum.toString(),
                R.drawable.delete,
                iconTint = Color(0xFFEB3838),
                containerColor = Color(0xFFF5AAC3)
            )
        }
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TodayCardItem(
                "共学习",
                today.learnTime.toString() + " 分钟",
                R.drawable.time,
                iconTint = Color(0xFF118EF1),
                containerColor = Color(0xFFA2D2F8)
            )
            Spacer(modifier = Modifier.width(10.dp))
            TodayCardItem(
                "打开App",
                today.openNum.toString() + " 次",
                R.drawable.open,
                iconTint = Color(0xFF118EF1),
                containerColor = Color(0xFFA2D2F8)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
fun TodayCardItem(
    title: String,
    value: String,
    icon: Int,
    iconTint: Color,
    containerColor: Color
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(10.dp),
    ) {
        Row(
            modifier = Modifier
                .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp)
                .size(width = 150.dp, height = Dp.Infinity),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = iconTint
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = title)
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = value,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
            )
        }
    }
}
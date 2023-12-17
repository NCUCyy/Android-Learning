package com.cyy.transapp.activity.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cyy.transapp.R
import com.cyy.transapp.TransApp
import com.cyy.transapp.model.OpResult
import com.cyy.transapp.model.trans.TransRes
import com.cyy.transapp.model.trans.Web
import com.cyy.transapp.pojo.TransRecord
import com.cyy.transapp.service.VoiceService
import com.cyy.transapp.view_model.TransRecordViewModel
import com.cyy.transapp.view_model.TransRecordViewModelFactory
import com.cyy.transapp.view_model.TransViewModel
import com.cyy.transapp.view_model.TransViewModelFactory

class TransActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userId = intent.getIntExtra("userId", 0)
        Log.i("TransActivity", "userId: $userId")
        val query = intent.getStringExtra("query")!!.trim()
        setContent {
            TransScreen(query, userId)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun TransScreen(query: String = "Dec", userId: Int = 0) {
    val context = LocalContext.current as Activity
    val application = LocalContext.current.applicationContext as TransApp
    val transViewModel =
        viewModel<TransViewModel>(
            factory = TransViewModelFactory(
                application.transRepository,
                userId,
                query,
                application.starWordRepository
            )
        )
    val isStared = transViewModel.isStared.collectAsState()
    // 脚手架
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // TODO：显示查询的词汇
                    Text(text = query, fontWeight = FontWeight.Bold, fontSize = 25.sp, maxLines = 1)
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
                    if (isStared.value) {
                        IconButton(onClick = {
                            // TODO：取消收藏
                            transViewModel.unstarWord()
                        }, modifier = Modifier.padding(end = 16.dp)) {
                            Icon(
                                painter = painterResource(id = R.drawable.star_fill),
                                contentDescription = null,
                                Modifier.size(30.dp),
                                tint = Color(0xFFE8C11C)
                            )
                        }
                    } else {
                        IconButton(onClick = {
                            // TODO：收藏
                            transViewModel.starWord()
                        }, modifier = Modifier.padding(end = 16.dp)) {
                            Icon(
                                painter = painterResource(id = R.drawable.star),
                                contentDescription = null,
                                Modifier.size(30.dp),
                                tint = Color(0xFFE8C11C)
                            )
                        }
                    }
                }
            )
        },
        content = {
            // 页面的主体部分
            Box(modifier = Modifier.padding(it)) {
                ContentScreen(query, userId, transViewModel)
            }
        },
        floatingActionButton = {
        })
}

@Composable
fun ContentScreen(query: String, userId: Int, transViewModel: TransViewModel) {
    val application = LocalContext.current.applicationContext as TransApp

    val transRecordViewModel =
        viewModel<TransRecordViewModel>(
            factory = TransRecordViewModelFactory(
                application.transRepository
            )
        )
//
//    LaunchedEffect(key1 = query) {
//        transViewModel.translate(query)
//    }
    val transState = transViewModel.transState.collectAsState()
    Box(modifier = Modifier.fillMaxSize()) {
        when (transState.value) {
            is OpResult.Success -> {
                // TODO：显示翻译结果
                val transRecord = TransRecord(
                    userId = userId,
                    word = query,
                    trans = ((transState.value as OpResult.Success<*>).data as TransRes).translation[0],
                )
                transRecordViewModel.updateHistory(transRecord)
                TransDetailScreen((transState.value as OpResult.Success<*>).data as TransRes)
            }

            is OpResult.Error -> {
                // TODO：显示错误信息
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Button(
                        onClick = {
                            // TODO：重新翻译
                            transViewModel.translate(query)
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
                                text = (transState.value as OpResult.Error<Any>).errorDesc.toString(),
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
}

val startBorder = 25.dp
var maxWidth = 260.dp

@Composable
fun TransDetailScreen(transRes: TransRes) {
    // 7个为一行
    val translation = transRes.translation
    val examTypes = transRes.basic.examType.chunked(6)
    val explains = transRes.basic.explains
    val web = transRes.web
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // 1、美式、英式发音---us-phonetic、uk-phonetic
        Row(
            modifier = Modifier
                .padding(start = 10.dp)
                .fillMaxWidth(),
            // 保持水平
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (transRes.basic.usPhonetic.isNotEmpty())
                VoiceItem(
                    type = "美",
                    phonetic = transRes.basic.usPhonetic,
                    url = transRes.basic.usSpeech
                )
            if (transRes.basic.ukPhonetic.isNotEmpty())
                VoiceItem(
                    type = "英",
                    phonetic = transRes.basic.ukPhonetic,
                    url = transRes.basic.ukSpeech
                )
        }
        Spacer(modifier = Modifier.height(10.dp))
        // 2、考试类型
        Column(modifier = Modifier.padding(start = startBorder, end = startBorder)) {
            examTypes.forEach { row: List<String> ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    row.forEach { type: String ->
                        ExamTypeCard(type)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "简明",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = startBorder)
        )
        TitleBodyDivider()
        Column {
            translation.forEach {
                Text(
                    text = it,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(start = startBorder)
                )
            }
        }
        Spacer(modifier = Modifier.height(40.dp))
        if (transRes.isWord) {
            // 3、基本释义
            Text(
                text = "基本释义",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                modifier = Modifier.padding(start = startBorder)
            )
            TitleBodyDivider()
            BasicExplains(explains)
        }
        Spacer(modifier = Modifier.height(30.dp))
        // 4、网络释义---web
        if (web.isNotEmpty()) {
            Text(
                text = "网络释义",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                modifier = Modifier.padding(start = startBorder)
            )
            TitleBodyDivider()
            WebExplains(web)
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun TitleBodyDivider() {
    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        color = Color(0xFFDAD9D9),
    )
}

@Composable
fun BasicExplains(explains: List<String>) {
    // 3、翻译结果---explains
    // .分割类型和解释/;分割多个解释
    val explainMap = mutableMapOf<String, List<String>>()
    for (i in explains.indices) {
        // lst[0] = "v."
        // lst[1] = ["自行车，摩托车；循环，周期；组诗，组歌；整套，系列；自行车骑行；一段时间"]
        val lst = explains[i].split(".")
        // 特殊情况：虽然是句子但是iwWord为True，此时explains中没有类型（所以lst只有一个元素）
        if (lst.size == 1) {
            explainMap["${i + 1}"] = listOf(lst[0])
            continue
        } else {
            val singleExplains = lst[1].trim().split("；")
            explainMap[lst[0]] = singleExplains
        }
    }
    // 得到的explainMap的格式为：{"v.", ["自行车, 摩托车", "循环, 周期"]}
    Column {
        explainMap.forEach { (type: String, singleExplains: List<String>) ->
            Row(modifier = Modifier.padding(bottom = 20.dp)) {
                ConstraintLayout {
                    val (typeRef, explainsRef) = createRefs()
                    val vGuideline = createGuidelineFromStart(startBorder)
                    val vGuideline2 = createGuidelineFromStart(startBorder + 65.dp)
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Black,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.constrainAs(typeRef) {
                            start.linkTo(vGuideline)
                        },
                        shape = RoundedCornerShape(5.dp)
                    ) {
                        Text(
                            text = "$type.",
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
                    Column(modifier = Modifier.constrainAs(explainsRef) {
                        start.linkTo(vGuideline2)
                    }) {
                        singleExplains.forEach {
                            Text(
                                text = it,
                                fontSize = 18.sp,
                                modifier = Modifier
                                    .padding(end = 10.dp)
                                    .size(height = Dp.Infinity, width = maxWidth + startBorder),
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun WebExplains(web: List<Web>) {
    web.forEach {
        Column(modifier = Modifier.padding(start = startBorder)) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ), shape = RoundedCornerShape(5.dp),
                modifier = Modifier.padding(5.dp)
            ) {
                Text(
                    text = it.key,
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
            Text(
                text = it.value.joinToString(";  "),
                modifier = Modifier
                    .padding(start = 5.dp, bottom = 10.dp, end = 10.dp)
                    .size(height = Dp.Infinity, width = maxWidth + startBorder + 40.dp),
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun ExamTypeCard(type: String) {
    Card(
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White, contentColor = Color.Gray),
        modifier = Modifier.padding(end = 15.dp),
        shape = RoundedCornerShape(5.dp)
    ) {
        Text(text = type, fontSize = 12.sp, modifier = Modifier.padding(5.dp))
    }
}

@Composable
fun VoiceItem(type: String, phonetic: String, url: String) {
    val context = LocalContext.current as Activity
    Button(
        onClick = {
            val intent = Intent(context, VoiceService::class.java)
            intent.putExtra("url", url)
            context.startService(intent)
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.DarkGray
        )
    ) {
        // 保持水平
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.voice),
                contentDescription = null,
                modifier = Modifier.size(
                    ButtonDefaults.IconSize
                )
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(text = "$type /$phonetic/", fontSize = 16.sp)
        }
    }
}

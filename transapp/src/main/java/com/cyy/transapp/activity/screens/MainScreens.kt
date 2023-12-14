package com.cyy.transapp.activity.screens

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.clickable
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cyy.transapp.R
import com.cyy.transapp.TransApp
import com.cyy.transapp.activity.StateHolder
import com.cyy.transapp.activity.TransActivity
import com.cyy.transapp.model.OpResult
import com.cyy.transapp.model.daily_sentence.SentenceModel
import com.cyy.transapp.pojo.TransRecord
import com.cyy.transapp.view_model.QueryViewModel
import com.cyy.transapp.view_model.QueryViewModelFactory

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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueryScreen(states: StateHolder) {
    val context = LocalContext.current as Activity
    val application = LocalContext.current.applicationContext as TransApp
    val queryViewModel = viewModel<QueryViewModel>(
        factory = QueryViewModelFactory(
            application.transRepository,
            application.queryRepository
        )
    )
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
                val intent = Intent(context, TransActivity::class.java)
                intent.putExtra("query", query.value)
                states.resultLauncher.launch(intent)
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
                        (sentenceState.value as OpResult.Success<*>).data as SentenceModel, states
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
        // 3、查词历史
        Column(modifier = Modifier.verticalScroll(scrollState)) {
            transRecords.value.forEachIndexed { idx: Int, transRecord: TransRecord ->
                TransRecordCard(
                    transRecord = transRecord,
                    isLast = idx == transRecords.value.size - 1,
                    states = states
                )
            }
        }
    }
}

@Composable
fun DailySentenceCard(sentenceModel: SentenceModel, states: StateHolder) {
    val context = LocalContext.current as Activity
    val data = sentenceModel.data
    Card(
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
        shape = RoundedCornerShape(5.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFEBF4FA),
            contentColor = Color.Black
        ), modifier = Modifier.clickable {
            toTransActivity(context, states, data.en)
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
fun TransRecordCard(transRecord: TransRecord, isLast: Boolean, states: StateHolder) {
    val context = LocalContext.current as Activity
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                // 点击卡片即可翻译查询
                toTransActivity(context, states, transRecord.word)
            }, colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        ConstraintLayout {
            val (iconRef, wordRef, transRef, freqRef) = createRefs()
            val vGuideline1 = createGuidelineFromStart(35.dp)
            val hGuideline = createGuidelineFromTop(0.5f)
            val vGuideline2 = createGuidelineFromStart(300.dp)
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

@Composable
fun ListenScreen() {

}

@Composable
fun LearnScreen() {

}

/**
 * 跳转到TransActivity---进行翻译
 */
fun toTransActivity(context: Activity, states: StateHolder, query: String) {
    val intent = Intent(context, TransActivity::class.java)
    intent.putExtra("query", query)
    states.resultLauncher.launch(intent)
}
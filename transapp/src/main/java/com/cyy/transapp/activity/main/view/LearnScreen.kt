package com.cyy.transapp.activity.main.view

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cyy.transapp.R
import com.cyy.transapp.activity.main.LearnActivity
import com.cyy.transapp.activity.main.ReviewActivity
import com.cyy.transapp.activity.main.StateHolder
import com.cyy.transapp.activity.other.VocabularySettingActivity
import com.cyy.transapp.activity.other.WordActivity
import com.cyy.transapp.activity.other.syncFontSize
import com.cyy.transapp.model.LearnProcess
import com.cyy.transapp.model.OpResult
import com.cyy.transapp.view_model.learn_review.LearnReviewViewModel
import com.google.gson.Gson


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
                LearnAndReviewCard(states, learnReviewViewModel)
                TodayCard(states, learnReviewViewModel)
            }

            is OpResult.Loading -> {
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
                    // TODO：跳转到VocabularySettingActivity(选择字典)
                    toVocabularySettingActivity(
                        curUser.value.id,
                        curUser.value.vocabulary,
                        context,
                        states.resultLauncher
                    )
                } else {
                    // TODO：跳转到WordActivity(查看所有单词)
                    val intent = Intent(context, WordActivity::class.java)
                    intent.putExtra("userId", curUser.value.id)
                    intent.putExtra("vocabulary", curUser.value.vocabulary)
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
                fontWeight = FontWeight.W900,
                fontSize = (20 + syncFontSize.value).sp,
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
            fontWeight = FontWeight.W900,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            color = Color(0xFF269C2A)
        )
        Text(
            text = if (curUser.value.vocabulary != "未选择") "共计 $vocabularySize 个单词" else "",
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
                fontWeight = FontWeight.W900,
                fontSize = (20 + syncFontSize.value).sp,
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
                    fontWeight = FontWeight.W900,
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
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        if (plan.value.vocabulary != "") {
            // 为""表示还没查询好(先不显示)
            if (plan.value.vocabulary != "未选择") {
                // 有选择
                // TODO：Learn-Part
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFCEF0FF)),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
                    modifier = Modifier
                        .size(width = 175.dp, height = Dp.Infinity)
                        .clickable {
                            val learnProcess = learnReviewViewModel.getLearnProcess(plan.value)
                            // TODO：若还有没复习的，询问是否要先复习
                            if (learnProcess.process.size > 0) {
                                // 还有词
                                // TODO：跳转到LearnActivity
                                val intent = Intent(context, LearnActivity::class.java)
                                intent.putExtra("userId", curUser.value.id)
                                intent.putExtra("vocabulary", plan.value.vocabulary)
                                states.resultLauncher.launch(intent)
                            } else {
                                // 没词了
                                // TODO：开启下一组学习（弹窗确认）
                                learnReviewViewModel.initLearnProcess()
                            }
                        }
                ) {
                    // 有选择
                    val learnProcess = learnReviewViewModel.getLearnProcess(plan.value)
                    if (learnProcess.process.size > 0) {
                        // 还有词
                        LearnReviewText("Learn", learnProcess.process.size)
                    } else {
                        // 没词了
                        Text(text = "学习下一组")
                    }
                }
                Spacer(modifier = Modifier.width(15.dp))
                // TODO：Review-Part
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1FFE1)),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
                    modifier = Modifier
                        .size(width = 175.dp, height = Dp.Infinity)
                        .clickable {
                            // TODO：跳转到ReviewActivity
                            val intent = Intent(context, ReviewActivity::class.java)
                            intent.putExtra("userId", curUser.value.id)
                            intent.putExtra("vocabulary", curUser.value.vocabulary)
                            states.resultLauncher.launch(intent)
                        }
                ) {
                    val reviewProcess = learnReviewViewModel.getReviewProcess(plan.value)
                    LearnReviewText("Review", reviewProcess.process.size)
                }
            } else {
                Card(
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            // TODO：跳转到VocabularyActivity---选择词汇
                            toVocabularySettingActivity(
                                curUser.value.id,
                                curUser.value.vocabulary,
                                context,
                                states.resultLauncher
                            )
                        },
                ) {
                    // 没选择
                    Text(
                        text = "请选择单词本",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 25.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun LearnReviewText(type: String, num: Int) {
    Column(
        modifier = Modifier
            .padding(start = 20.dp, top = 10.dp, bottom = 10.dp)
    ) {
        Text(text = type, fontWeight = FontWeight.W900, fontSize = (25 + syncFontSize.value).sp)
        Text(
            text = num.toString(),
            color = Color(0xFFFF5722),
            fontWeight = FontWeight.Bold,
            fontSize = (20 + syncFontSize.value).sp,
        )
    }
}

// 跳转到VocabularyActivity---选Vocabulary
fun toVocabularySettingActivity(
    userId: Int,
    vocabulary: String,
    context: Activity,
    resultLauncher: ActivityResultLauncher<Intent>
) {
    val intent = Intent(context, VocabularySettingActivity::class.java)
    intent.putExtra("userId", userId)
    intent.putExtra("vocabulary", vocabulary)
    resultLauncher.launch(intent)
}


@Composable
fun DailyAttendanceCard(states: StateHolder, learnReviewViewModel: LearnReviewViewModel) {
    val todaysState = learnReviewViewModel.todays.collectAsStateWithLifecycle()
    // 初始化
    val todays = mutableListOf<Int>().apply {
        for (today in todaysState.value) {
            this.add(today.day)
        }
    }
    val now = learnReviewViewModel.now
    val days = mutableListOf<Int>()
    for (i in 1..now.dayOfMonth) {
        days.add(i)
    }
    days.reverse()
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
            text = "Daily Attendance", fontWeight = FontWeight.W900,
            fontSize = (20 + syncFontSize.value).sp,
            modifier = Modifier.padding(10.dp)
        )
        LazyRow(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            items(days) { day: Int ->
                DailyCardItem(day, now.month.name, todays)
            }
        }
    }
}

@Composable
fun DailyCardItem(day: Int, month: String, days: List<Int>) {
    val iconSize = 40.dp
    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F3F3))) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val showMonth = month[0] + month.lowercase().substring(1, 3)
            Text(text = "$showMonth $day", color = Color(0xFF269C2A), fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))
            if (days.contains(day)) {
                Icon(
                    painter = painterResource(id = R.drawable.done),
                    contentDescription = null,
                    tint = Color(0xFFFFC107),
                    modifier = Modifier
                        .size(iconSize)
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.undone),
                    contentDescription = null,
                    tint = Color(0xFFC70044),
                    modifier = Modifier.size(iconSize)
                )
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
            fontWeight = FontWeight.W900,
            fontSize = (20 + syncFontSize.value).sp,
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
                containerColor = Color(0xFFF5DBD3)
            )
            Spacer(modifier = Modifier.width(10.dp))
            TodayCardItem(
                "新学单词",
                today.newLearnNum.toString(),
                R.drawable.book,
                iconTint = Color(0xFF476D1A),
                containerColor = Color(0xFFF5DBD3)
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
                containerColor = Color(0xFFFDF8CA)
            )
            Spacer(modifier = Modifier.width(10.dp))
            TodayCardItem(
                "移除单词",
                today.removeNum.toString(),
                R.drawable.delete,
                iconTint = Color(0xFFEB3838),
                containerColor = Color(0xFFFCCADB)
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
                containerColor = Color(0xFFC3DFF5)
            )
            Spacer(modifier = Modifier.width(10.dp))
            TodayCardItem(
                "打开App",
                today.openNum.toString() + " 次",
                R.drawable.open,
                iconTint = Color(0xFF118EF1),
                containerColor = Color(0xFFC3DFF5)
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
package com.cyy.exp2.psychological_test.ui

import android.annotation.SuppressLint
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.cyy.exp2.R
import com.cyy.exp2.memo.Memo
import com.cyy.exp2.memo.MemoCard
import com.cyy.exp2.memo.MemoViewModel
import com.cyy.exp2.psychological_test.pojo.Record
import com.cyy.exp2.psychological_test.view_model.SentenceViewModel
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

val screens = listOf(Screen.HomePage, Screen.HistoryPage, Screen.UserPage)

/**
 *Screen类（与用于显示的Screen实体不同！要区分开！Screen类只用于提供页面需要的元数据metaData：icon、title、"route"【用于导航】）
 */
sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object HomePage :
        Screen(route = "home", title = "", icon = Icons.Filled.Home)

    object HistoryPage :
        Screen(route = "testHistory", title = "答题记录", icon = Icons.Filled.List)

    object UserPage :
        Screen(route = "user", title = "个人主页", icon = Icons.Filled.AccountCircle)
}

@Preview
@Composable
fun HomeScreen(resultLauncher: ActivityResultLauncher<Intent>? = null) {
    // 永远获得同一个ViewModel----前提是在一个Activity之内
    // 创建一个 ViewModelProvider 实例
    val viewModelProvider = ViewModelProvider(LocalContext.current as ViewModelStoreOwner)
    // 获取指定类型的 ViewModel
    val sentenceViewModel = viewModelProvider[SentenceViewModel::class.java]

    val sentence = sentenceViewModel.sentence.collectAsState().value

    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier
                .padding(10.dp),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White,
            ),
        ) {
            Row {
                Text(
                    text = "每日一句",
                    fontWeight = FontWeight.Bold,
                    fontSize = 40.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(start = 10.dp, top = 10.dp),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 40.sp,
                        fontStyle = FontStyle.Italic
                    ),
                )
                IconButton(modifier = Modifier.padding(top = 20.dp), onClick = {
                    sentenceViewModel.shuffleSentence()
                }) {
                    Icon(imageVector = Icons.Filled.Refresh, contentDescription = null)
                }
            }
            AsyncImage(
                model = sentence.data.pic,
                placeholder = painterResource(R.drawable.ic_launcher_background),
                contentDescription = stringResource(R.string.app_name),
                contentScale = ContentScale.Crop,
                modifier = Modifier.padding(10.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = sentence.data.en,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 10.dp, end = 10.dp)
            )

            Text(
                text = sentence.data.zh,
                fontSize = 15.sp,
                color = Color.Gray,
                modifier = Modifier.padding(10.dp)
            )
        }
        Button(
            onClick = {
                val intent = Intent(context as Activity, QuizActivity::class.java)
                resultLauncher!!.launch(intent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 5.dp),
            shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF66CDAA),
                contentColor = Color.White
            )
        ) {
            Text("Get Started", fontSize = 20.sp)
        }
    }
}


@Composable
fun HistoryScreen(records: State<List<Record>>) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
        )
    ) {
        LazyColumn {
            items(records.value.reversed()) { record ->
                RecordCard(record)
            }
        }
    }
}


@SuppressLint("RememberReturnType")
@Composable
fun RecordCard(record: Record) {
    val containColorState = remember { mutableStateOf(Color.White) }
    val contentColorState = remember { mutableStateOf(Color.Black) }
    if (record.score < 10) {
        containColorState.value = Color(0xFFDA4D7D)
        contentColorState.value = Color.White
    } else {
        containColorState.value = Color(0xFF66CDAA)
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .clickable {
                // TODO
            },
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
            contentColor = Color.Black
        )
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
            var (scoreRef, timeRef) = remember {
                createRefs()
            }
            val vGuideline = createGuidelineFromStart(0.3f)
//            val hGuideline = createGuidelineFromTop(0.7f)

            Card(shape = RoundedCornerShape(40.dp),
                colors = CardDefaults.cardColors(
                    containerColor = containColorState.value,
                    contentColor = contentColorState.value
                ),
                modifier = Modifier
                    .size(80.dp)
                    .padding(10.dp)
                    .constrainAs(scoreRef) {
                        start.linkTo(parent.start)
                        end.linkTo(vGuideline)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = record.score.toString(),
                        fontSize = 20.sp,
                    )
                }
            }
            // 转化时间表示方式，用于显示
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm:ss")
            val testTime = record.testTme.format(formatter)
            Text(text = "答题时间：${testTime}", modifier = Modifier
                .constrainAs(timeRef) {
                    start.linkTo(vGuideline)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
                .padding(10.dp))
        }
    }
}


@Composable
fun UserScreen() {

}
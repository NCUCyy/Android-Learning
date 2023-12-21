package com.cyy.transapp.activity.other

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cyy.transapp.R
import com.cyy.transapp.TransApp
import com.cyy.transapp.activity.main.view.toTransActivity
import com.cyy.transapp.pojo.StarWord
import com.cyy.transapp.util.TimeUtil
import com.cyy.transapp.view_model.StarWordViewModel

class StarWordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userId = intent.getIntExtra("userId", 14)
        // 使用ActivityResultLauncher进行意图跳转
        val resultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            // 意图结束后，执行这个「回调函数」
            ActivityResultCallback {

            }
        )
        setContent {
            StarWordMainScreen(userId, resultLauncher)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StarWordMainScreen(userId: Int, resultLauncher: ActivityResultLauncher<Intent>) {
    val context = LocalContext.current as Activity
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "生词本",
                            textAlign = TextAlign.Center
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
            // 页面的主体部分
            Box(modifier = Modifier.padding(it)) {
                // 侧滑导航视图（侧滑界面+导航图）
                StarWordListScreen(userId, resultLauncher)
            }
        },
        floatingActionButton = {
        })
}

@Composable
fun StarWordListScreen(userId: Int = 0, resultLauncher: ActivityResultLauncher<Intent>) {
    val application = LocalContext.current.applicationContext as TransApp
    val starWordViewModel = StarWordViewModel(userId, application.starWordRepository)
    val starWords = starWordViewModel.starWords.collectAsStateWithLifecycle()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 10.dp, end = 10.dp)
    ) {
        items(starWords.value) { starWord: StarWord ->
            StarWordCard(starWord, resultLauncher, userId)
        }
    }
}

@Composable
fun StarWordCard(starWord: StarWord, resultLauncher: ActivityResultLauncher<Intent>, userId: Int) {
    val context = LocalContext.current as Activity
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth()
            .clickable {
                // 点击直接查询
                toTransActivity(context, resultLauncher, starWord.word, userId)
            },
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        ConstraintLayout {
            val (wordRef, addTimeRef) = createRefs()
            val vGuideline = createGuidelineFromStart(0.5f)
            val hGuideline = createGuidelineFromTop(0.5f)
            Text(text = starWord.word, modifier = Modifier.constrainAs(wordRef) {
                start.linkTo(parent.start)
                end.linkTo(vGuideline)
                top.linkTo(parent.top, margin = 20.dp)
                bottom.linkTo(parent.bottom, margin = 20.dp)
            }, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text(
                color = Color.Gray,
                fontSize = 15.sp,
                text = TimeUtil.formatTime(starWord.addTime),
                modifier = Modifier.constrainAs(addTimeRef) {
                    start.linkTo(vGuideline)
                    end.linkTo(parent.end)
                    top.linkTo(hGuideline)
                    bottom.linkTo(parent.bottom)
                }
            )
        }
    }
}
package com.cyy.exp2.daily_word_app.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cyy.exp2.daily_word_app.DailyWordApp
import com.cyy.exp2.daily_word_app.pojo.Record
import com.cyy.exp2.daily_word_app.view_model.QuizViewModel
import com.cyy.exp2.daily_word_app.view_model.QuizViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.OffsetDateTime

class QuizActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 从MainActivity中传过来的：题库种类
        val category = intent.getStringExtra("category")!!
        val username = intent.getStringExtra("username")!!

        // 从ResultActivity中传过来的：用于判断是否需要直接返回主界面
        val returnToMain = mutableStateOf(false)
        // 从ResultActivity中传过来的：用于存储返回的答题记录
        val record: MutableState<Record?> = mutableStateOf(null)

        // 使用ActivityResultLauncher进行意图跳转
        val resultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            // 意图结束后，执行这个「回调函数」
            ActivityResultCallback {
                if (it.resultCode == RESULT_OK && it.data?.hasExtra("record")!!) {
                    // 返回的data数据是个intent类型，里面存储了一段文本内容
                    record.value = it.data?.getParcelableExtra("record", Record::class.java)
                    returnToMain.value = true
                }
            }
        )
        setContent {
            if (returnToMain.value) {
                val context = LocalContext.current as Activity
                val intent = Intent()
                intent.putExtra("record", record.value)
                context.setResult(Activity.RESULT_OK, intent)
                context.finish()
            } else {
                MainScreen(category, username, resultLauncher)
            }
        }
    }
}

// 主界面
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(category: String, username: String, resultLauncher: ActivityResultLauncher<Intent>) {
    val application = LocalContext.current.applicationContext as DailyWordApp
    val quizViewModel = viewModel<QuizViewModel>(
        factory = QuizViewModelFactory(
            application.quizRepository, category, username
        )
    )
    val curQuizIdx = quizViewModel.curQuizIdx.collectAsState().value
    val showQuizzesDialog = remember { mutableStateOf(false) }
    val showResultDialog = remember { mutableStateOf(false) }
    val showExitDialog = remember { mutableStateOf(false) }
    // 页面骨架的脚手架
    Scaffold(
        //定义头部
        topBar = {
            // 定义顶部栏需要解决两个问题：
            // （1）需要在顶部栏定义顶部的右侧导航菜单
            // （2）需要定义顶部的导航按钮，使得启动侧滑菜单
            TopAppBar(
                // 左侧文本
                title = {
                    Text(
                        quizViewModel.category,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                // 左侧图标
                navigationIcon = {
                    IconButton(onClick = {
                        // TODO：返回MainActivity
                        showExitDialog.value = true
                    }) {
                        Icon(Icons.Filled.KeyboardArrowLeft, contentDescription = null)
                    }
                },
                // 右侧按钮————按行处理的交互
                actions = {
                    IconButton(onClick = {
                        showResultDialog.value = true
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = "Localized description"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(containerColor = Color.Transparent) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            modifier = Modifier.padding(10.dp),
                            onClick = {
                                quizViewModel.lastQuiz()
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE0FFFF),
                                contentColor = Color.Black
                            ),
                            elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 10.dp),
                        ) {
                            Icon(
                                // Material 库中的图标，Icons.Filled下面有很多自带的图标
                                Icons.Filled.ArrowBack,
                                contentDescription = null,
                                modifier = Modifier.size(ButtonDefaults.IconSize)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = "上一题", fontSize = 20.sp)
                        }

                        TextButton(modifier = Modifier.padding(top = 6.dp),
                            onClick = {
                                // 显示答题卡
                                showQuizzesDialog.value = true
                            }
                        ) {
                            Text(
                                text = "${curQuizIdx + 1}/${quizViewModel.selected.value.size}",
                                fontSize = 20.sp, fontWeight = FontWeight.Bold
                            )
                        }
                        if (curQuizIdx == quizViewModel.selected.value.size - 1) {
                            Button(
                                modifier = Modifier.padding(10.dp),
                                onClick = {
                                    // TODO：提交答案
                                    showResultDialog.value = true
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFE0FFFF),
                                    contentColor = Color.Black
                                ),
                                elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 10.dp),
                            ) {
                                Text(text = "提交", fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Icon(
                                    Icons.Filled.Done,
                                    contentDescription = null,
                                    modifier = Modifier.size(ButtonDefaults.IconSize)
                                )
                            }
                        } else {
                            Button(
                                modifier = Modifier.padding(10.dp),
                                onClick = {
                                    quizViewModel.nextQuiz()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFE0FFFF),
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(10.dp),
                                elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 10.dp),
                            ) {
                                Text(text = "下一题", fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Icon(
                                    Icons.Filled.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(ButtonDefaults.IconSize)
                                )
                            }
                        }
                    }
                }
            }
        },
        //定义悬浮按钮
        floatingActionButton = {
            // TODO：显示当前答题的的Dialog
        },
        // 主体
        content = {
            // 页面的主体部分
            Box(modifier = Modifier.padding(it)) {
                QuizScreen(
                    quizViewModel,
                    showQuizzesDialog,
                    showResultDialog,
                    showExitDialog,
                    resultLauncher
                )
            }
        },
    )
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun QuizScreen(
    quizViewModel: QuizViewModel,
    showQuizzesDialog: MutableState<Boolean>,
    showResultDialog: MutableState<Boolean>,
    showExitDialog: MutableState<Boolean>,
    resultLauncher: ActivityResultLauncher<Intent>
) {
    val context = LocalContext.current as Activity
    val curQuiz = quizViewModel.curQuiz.collectAsState().value
    Card(
        modifier = Modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = curQuiz.question,
                fontWeight = FontWeight.Bold,
                fontSize = 40.sp,
                modifier = Modifier.padding(10.dp)
            )
        }

        Column {
            curQuiz.options.forEach { it ->
                OptionCard(
                    option = it,
                    quizViewModel
                )
            }
        }
    }
    if (showQuizzesDialog.value) {
        QuizzesDialog(showQuizzesDialog, quizViewModel)
    }
    if (showResultDialog.value) {
        ResultDialog(showResultDialog, quizViewModel, resultLauncher)
    }
    if (showExitDialog.value) {
        ConfirmExitDialog(showExitDialog)
    }
}

@Composable
fun ConfirmExitDialog(showExitDialog: MutableState<Boolean>) {
    val context = LocalContext.current as Activity
    Dialog(onDismissRequest = {
        showExitDialog.value = false
    }) {
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White,
                ),
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = "请确认是否退出",
                    modifier = Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    fontSize = 20.sp,
                    text = "退出后将不会保存答题记录",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = Color(0xFFB1B3B3)
                )
                Spacer(modifier = Modifier.height(15.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            showExitDialog.value = false
                        },
                        shape = RoundedCornerShape(15.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFA85AD),
                            contentColor = Color.Black
                        ),
                    ) {
                        Text(text = "取消", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = null,
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Button(
                        onClick = {
                            showExitDialog.value = false
                            context.finish()
                        },
                        shape = RoundedCornerShape(15.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF88D4F7),
                            contentColor = Color.Black
                        ),
                    ) {
                        Text(text = "确认", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Icon(
                            Icons.Filled.Done,
                            contentDescription = null,
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(15.dp))
            }
        }

    }
}

@Composable
fun OptionCard(option: String ,quizViewModel: QuizViewModel) {
    val scope = rememberCoroutineScope()
    val containColorState = remember { mutableStateOf(Color.White) }
    val contentColoState = remember { mutableStateOf(Color.Black) }
    val answer = quizViewModel.curQuiz.collectAsState().value.answer
    val curOption = quizViewModel.curOption.collectAsState().value

    LaunchedEffect(curOption) {
        containColorState.value = Color.White
        contentColoState.value = Color.Black
        if (curOption != "") {
            // 若当前选项为正确答案，则显示绿色
            if (answer == curOption) {
                if (option == curOption) {
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
            .padding(10.dp)
            .fillMaxWidth()
            .clickable {
                if (curOption == "")
                    quizViewModel.setOption(option)
                // 过两秒自动跳转到下一题
                scope.launch {
                    delay(1000)
                    quizViewModel.nextQuiz()
                }
            },
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
        colors = CardDefaults.cardColors(
            containerColor = containColorState.value,
            contentColor = contentColoState.value
        )
    ) {
        Text(
            text = option,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(18.dp)
        )
    }
}


@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun QuizzesDialog(showQuizzesDialog: MutableState<Boolean>, quizViewModel: QuizViewModel) {
    val selected = quizViewModel.selected.collectAsState().value
    // 使用LazyColumn和LazyRow来布局
    val chunked = selected.chunked(5)
    Dialog(onDismissRequest = {
        showQuizzesDialog.value = false
    }) {
        Box(
            modifier = Modifier.background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Card(
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White,
                ),
            ) {
                Text(
                    text = "答题卡",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 15.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(10.dp))
                Column(modifier = Modifier.padding(start = 10.dp, end = 10.dp)) {
                    chunked.forEachIndexed { rIndex, rowLst ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            rowLst.forEachIndexed() { cIndex, item ->
                                // 当前遍历到的题目序号(0开始)
                                val idx = rIndex * 5 + cIndex
                                QuizCard(
                                    index = idx + 1,
                                    answer = item,
                                    quizViewModel,
                                    onClicked = {
                                        showQuizzesDialog.value = false
                                        quizViewModel.setCurQuizIdx(idx)
                                    })
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

        }
    }
}

@Composable
fun QuizCard(index: Int, answer: String, quizViewModel: QuizViewModel, onClicked: () -> Unit) {
    val containColorState = remember { mutableStateOf(Color(0xFFB1B3B3)) }
    val contentColorState = remember { mutableStateOf(Color.Black) }
    val curQuiz = quizViewModel.quizzes[index - 1]
    if (answer == "") {
        containColorState.value = Color(0xFFB1B3B3)
        contentColorState.value = Color.Black
    } else {
        if (curQuiz.answer == answer) {
            containColorState.value = Color(0xFF98FB98)
            contentColorState.value = Color.Black
        } else {
            containColorState.value = Color(0xFFE90C57)
            contentColorState.value = Color.White
        }
    }
    Card(
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
        modifier = Modifier
            .size(60.dp)
            .padding(8.dp)
            .clickable {
                onClicked()
            },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = containColorState.value,
            contentColor = contentColorState.value
        )
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = index.toString())
        }
    }
}


@SuppressLint(
    "RememberReturnType",
    "StateFlowValueCalledInComposition",
    "UnrememberedMutableState"
)

@Composable
fun ResultDialog(
    showResultDialog: MutableState<Boolean>,
    quizViewModel: QuizViewModel,
    resultLauncher: ActivityResultLauncher<Intent>
) {
    val context = LocalContext.current as Activity
    val right = quizViewModel.right.collectAsState()
    val undo = quizViewModel.undo.collectAsState()
    val wrong = quizViewModel.wrong.collectAsState()
    Dialog(onDismissRequest = {
        showResultDialog.value = false
    }) {
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White,
                ),
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = "您答对的题数：${right.value}/${quizViewModel.selected.value.size}",
                    modifier = Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(15.dp))
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 30.dp, end = 30.dp),
                    onClick = {
                        // TODO：重新开始
                        quizViewModel.reset()
                        showResultDialog.value = false
                    },
                    shape = RoundedCornerShape(15.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFA85AD),
                        contentColor = Color.Black
                    ),
                ) {
                    Text(text = "重新开始", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Icon(
                        Icons.Filled.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 30.dp, end = 30.dp, bottom = 20.dp),
                    onClick = {
                        // TODO：跳转到ResultActivity
                        showResultDialog.value = false
                        val testDuration = quizViewModel.getTestDuration()
                        var intent = Intent(context, ResultActivity::class.java)
                        val record = Record(
                            testTime = OffsetDateTime.now(),
                            right = right.value,
                            wrong = wrong.value,
                            undo = undo.value,
                            duration = testDuration,
                            category = quizViewModel.category
                        )
                        intent.putExtra("record", record)
                        intent.putExtra("username", quizViewModel.username)

                        context.setResult(Activity.RESULT_OK, intent)
                        resultLauncher.launch(intent)
                    },
                    shape = RoundedCornerShape(15.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF88D4F7),
                        contentColor = Color.Black
                    ),
                ) {
                    Text(text = "完成答题", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Icon(
                        Icons.Filled.Done,
                        contentDescription = null,
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                }
            }
        }

    }
}
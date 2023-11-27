package com.cyy.exp2.psychological_test.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material.icons.filled.Home
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cyy.exp2.psychological_test.PsychologicalTestApp
import com.cyy.exp2.psychological_test.pojo.Record as MyRecord
import com.cyy.exp2.psychological_test.view_model.QuizViewModel
import com.cyy.exp2.psychological_test.view_model.QuizViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.OffsetDateTime

class QuizActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }
}

// 主界面
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun MainScreen() {
    val application = LocalContext.current.applicationContext as PsychologicalTestApp
    val quizViewModel = viewModel<QuizViewModel>(
        factory = QuizViewModelFactory(
            application.quizRepository,
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
                        "单词测验",
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
                        quizViewModel.commit()
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
                                    quizViewModel.commit()
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
                QuizScreen(quizViewModel, showQuizzesDialog, showResultDialog, showExitDialog)
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
    showExitDialog: MutableState<Boolean>
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
                    answer = curQuiz.answer,
                    quizViewModel
                )
            }
        }
    }
    if (showQuizzesDialog.value) {
        QuizzesDialog(showQuizzesDialog)
    }
    if (showResultDialog.value) {
        ResultDialog(showResultDialog, quizViewModel)
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
fun OptionCard(option: String, answer: String, quizViewModel: QuizViewModel) {
    val scope = rememberCoroutineScope()
    val containColorState = remember { mutableStateOf(Color.White) }
    val curOption = quizViewModel.curOption.collectAsState().value
    LaunchedEffect(curOption) {
        containColorState.value = Color.White
        if (curOption != "") {
            // 若当前选项为正确答案，则显示绿色
            if (answer == curOption) {
                if (option == curOption) {
                    containColorState.value = Color(0xFF98FB98)
                }
            } else {
                if (option == answer) {
                    containColorState.value = Color(0xFF98FB98)
                }
                if (option == curOption) {
                    containColorState.value = Color(0xFFDA4D7D)
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
fun QuizzesDialog(showQuizzesDialog: MutableState<Boolean>) {
    val application = LocalContext.current.applicationContext as PsychologicalTestApp
    val quizViewModel = viewModel<QuizViewModel>(
        factory = QuizViewModelFactory(
            application.quizRepository,
        )
    )
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
                            horizontalArrangement = Arrangement.SpaceAround
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
    val curQuiz = quizViewModel.quizzes[index - 1]
    if (answer == "") {
        containColorState.value = Color(0xFFB1B3B3)
    } else {
        if (curQuiz.answer == answer) {
            containColorState.value = Color(0xFF98FB98)
        } else {
            containColorState.value = Color(0xFFDA4D7D)
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
            contentColor = Color.Black
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
    quizViewModel: QuizViewModel
) {
    val score = quizViewModel.score.value
    val context = LocalContext.current as Activity
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
                    text = "您答对的题数：$score/${quizViewModel.selected.value.size}",
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
                        // TODO：返回MainActivity
                        showResultDialog.value = false
                        var intent = Intent()
                        intent.putExtra(
                            "record",
                            MyRecord(OffsetDateTime.now(), score = score!!, "TODO")
                        )
                        context.setResult(Activity.RESULT_OK, intent)
                        context.finish()
                    },
                    shape = RoundedCornerShape(15.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF88D4F7),
                        contentColor = Color.Black
                    ),
                ) {
                    Text(text = "返回主页", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Icon(
                        Icons.Filled.Home,
                        contentDescription = null,
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )

                }
            }
        }

    }
}
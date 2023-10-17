package com.cyy.exp1.diceGame

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.cyy.exp1.R
import android.content.Context
import android.service.autofill.FillEventHistory
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.compose.ui.res.integerArrayResource

class GameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 是否重启游戏
        var isStart = mutableStateOf(true)

        // 使用ActivityResultLauncher进行意图跳转
        val resultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            // 意图结束后，执行这个「回调函数」
            ActivityResultCallback {
                if (it.resultCode == Activity.RESULT_OK) {
                    // 设置重启游戏
                    isStart.value = true
                    // 返回的data数据是个intent类型，里面存储了一段文本内容
                    val text = it.data?.getStringExtra("message")
                    Toast.makeText(this, "接受：$text", Toast.LENGTH_LONG).show()
                }
            }
        )
        setContent {
            GameScreen(resultLauncher, isStart)
        }
    }
}

@Composable
fun GameScreen(resultLauncher: ActivityResultLauncher<Intent>, isStart: MutableState<Boolean>) {
    // 当前活动的上下文对象
    val context = LocalContext.current
    // 定义了游戏的业务逻辑
    val game = DiceGame()
    // 定义状态变量，与组件进行绑定————必须用remember：为了重组后，执行这个函数时仍然记住原来的状态值
    // 骰子1的状态
    val firstStatus = remember { mutableStateOf(0) }
    // 骰子2的状态
    val secondStatus = remember { mutableStateOf(0) }
    // 游戏状态(初始化为：GameStatus.START)
    val gameStatus = remember { mutableStateOf(GameStatus.START) }
    // 记录本轮历史
    val curHistory = remember { mutableListOf<String>() }
    // 记录总历史
    val history = remember { mutableListOf<MutableList<String>>() }
    // 是否展示规则
    val showRule = remember { mutableStateOf(false) }
    if (isStart.value) {
        init(firstStatus, secondStatus, gameStatus, curHistory)
        isStart.value = false
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column {
            // 规则按钮
            RuleBtn()
            // 两张Dice的照片
            DiceImg(firstStatus, secondStatus, gameStatus)
            // 游戏按钮
            PlayBtn(firstStatus, secondStatus, game, gameStatus, curHistory, history)


            // 监视gameStatus状态变量的值是否发生变化，若变化，则立刻更新页面
            if (gameStatus.value == GameStatus.WIN) {
                // 把本轮的历史加入总历史中
                history.add(curHistory.toMutableList())
                // 自定义对话框
                CustomAlertDialog(
                    context = context,
                    title = gameStatus.value.description,
                    activityType = GameWinActivity::class.java,
                    firstStatus, secondStatus, gameStatus, curHistory, history, resultLauncher
                )
            } else if (gameStatus.value == GameStatus.LOSE) {
                // 把本轮的历史加入总历史中
                history.add(curHistory.toMutableList())
                // 自定义对话框
                CustomAlertDialog(
                    context = context,
                    title = gameStatus.value.description,

                    activityType = GameLoseActivity::class.java,
                    firstStatus, secondStatus, gameStatus, curHistory, history, resultLauncher
                )
            }

        }
    }
}

@Composable
fun DiceImg(
    firstStatus: MutableState<Int>,
    secondStatus: MutableState<Int>,
    gameStatus: MutableState<GameStatus>
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.height(400.dp)
    ) {
        if (gameStatus.value != GameStatus.START) {
            CustomImage("骰子1", firstStatus.value)
            CustomImage("骰子2", secondStatus.value)
        }
    }
}

@Composable
fun PlayBtn(
    firstStatus: MutableState<Int>,
    secondStatus: MutableState<Int>,
    game: DiceGame,
    gameStatus: MutableState<GameStatus>,
    curHistory: MutableList<String>,
    history: MutableList<MutableList<String>>
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.height(400.dp)
    ) {
        Button(onClick = {
            // 1、抛骰子
            firstStatus.value = game.rollDice()
            secondStatus.value = game.rollDice()
            val total = firstStatus.value + secondStatus.value

            // 2、处理业务
            // 第一次和后几次的逻辑不同
            if (gameStatus.value == GameStatus.START) {
                // 若是第一次抛骰子，则调用judgeFirstTurn()的逻辑进行判断
                gameStatus.value =
                    game.judgeFirstTurn(total)

            } else {
                // 若已经抛过骰子，则调用judgeLaterTurn()的逻辑进行判断
                gameStatus.value =
                    game.judgeLaterTurn(
                        total,
                        gameStatus.value
                    )

            }

            // 3、更新状态的point值并把当前游戏状态加入历史
            gameStatus.value.updatePoint(total)
            // 加入本轮游戏历史
            curHistory.add("次数：${curHistory.size + 1}          点数：${gameStatus.value.point}")
        }, modifier = Modifier.fillMaxWidth()) {
            Text(text = history.toString())
        }
    }
}


// 重置界面参数(注意：history不需要重置)
fun init(
    firstStatus: MutableState<Int>,
    secondStatus: MutableState<Int>,
    gameStatus: MutableState<GameStatus>,
    curHistory: MutableList<String>
) {
    firstStatus.value = 0
    secondStatus.value = 0
    curHistory.clear()
    gameStatus.value = GameStatus.START
}

@Composable
fun <T> CustomAlertDialog(
    context: Context,
    title: String,
    activityType: Class<T>,
    firstStatus: MutableState<Int>,
    secondStatus: MutableState<Int>,
    gameStatus: MutableState<GameStatus>,
    curHistory: MutableList<String>,
    history: MutableList<MutableList<String>>,
    resultLauncher: ActivityResultLauncher<Intent>
) {
    // 状态变量showDialog------与AlertDialog组件的显示与否进行绑定！！！
    var showDialog by remember { mutableStateOf(true) }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                // 点击Dialogue以外的地方时执行的操作：do nothing
//                showDialog = false
//                // 重置界面
//                init(firstStatus, secondStatus, gameStatus, curHistory)
            },
            title = { Text(title) },
            text = {
                // 展示本轮的历史记录
                Column {
                    Row {
                        Text(text = "每次点数如下（共${curHistory.size}次）：")
                    }
                    // 超过6次展示，只展示后6条
                    if (curHistory.size > 6)
                        Row {
                            Text(text = "...")
                        }
                    curHistory.takeLast(6).forEach {
                        Row {
                            Text(text = it)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // 确定按钮点击时执行的操作
                        showDialog = false
                        // 重置界面
                        init(firstStatus, secondStatus, gameStatus, curHistory)
                    }
                ) {
                    Text(text = "继续")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        // 取消按钮点击时执行的操作
                        showDialog = false
                        // 跳转到指定Screen
                        turnScreen(context, title, activityType, history, resultLauncher)
                    }
                ) {
                    Text(text = "退出")
                }
            },
            modifier = Modifier.width(280.dp)
        )
    }
}


fun <T> turnScreen(
    context: Context,
    result: String,
    activityType: Class<T>,
    history: MutableList<MutableList<String>>,
    resultLauncher: ActivityResultLauncher<Intent>
) {
    val intent = Intent(context, activityType)
    intent.putExtra("result", result)
    intent.putExtra("history", ArrayList(history))
    // 意图跳转
    resultLauncher.launch(intent)
}

@Composable
fun CustomImage(description: String, imageId: Int) {
    Image(
        painter = painterResource(getImage(point = imageId)),
        contentDescription = "$description",
        modifier = Modifier
            .width(200.dp)
            .height(200.dp)
            .padding(start = 15.dp, end = 10.dp)
    )
}


fun getImage(point: Int): Int = when (point) {
    // 返回imageId
    1 -> R.mipmap.one
    2 -> R.mipmap.two
    3 -> R.mipmap.three
    4 -> R.mipmap.four
    5 -> R.mipmap.five
    6 -> R.mipmap.six
    else -> 0
}
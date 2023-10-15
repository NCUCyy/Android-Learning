package com.cyy.exp1.diceGame

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


class GameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GameScreen()
        }
    }
}

@Composable
fun GameScreen() {
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
    var gameStatus = remember { mutableStateOf(GameStatus.START) }
    // 记录本轮扔了几次
    var cnt = remember { mutableStateOf(0) }
    // 记录游戏的结果列表
    var history = remember { mutableListOf<String>() }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column {
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
                    cnt.value++
                    gameStatus.value.updatePoint(total)
                    // 加入游戏历史
                    history.add("次数：${cnt.value}  结果：${gameStatus.value.description}  点数：${gameStatus.value.point}")

                }) {
                    Text(text = history.toString())
                }

                // 监视gameStatus状态变量的值是否发生变化，若变化，则立刻更新页面
                if (gameStatus.value == GameStatus.WIN) {
                    // 获取当前的
                    val curHistory = getCurTurnHistory(history)
                    // 自定义对话框
                    CustomAlertDialog(
                        context = context,
                        title = gameStatus.value.description,
                        activityType = GameWinActivity::class.java,
                        firstStatus, secondStatus, gameStatus, cnt, curHistory
                    )
                } else if (gameStatus.value == GameStatus.LOSE) {
                    val curHistory = getCurTurnHistory(history)
                    // 自定义对话框
                    CustomAlertDialog(
                        context = context,
                        title = gameStatus.value.description,

                        activityType = GameLoseActivity::class.java,
                        firstStatus, secondStatus, gameStatus, cnt, curHistory
                    )
                }

            }
        }
    }
}

fun getCurTurnHistory(history: MutableList<String>): MutableList<String> {
    for (i in history.size - 1 downTo 0) {
        if (history[i][3] == '1') {
            return history.subList(i, history.size)
        }
    }
    return mutableListOf<String>()
}

// 重置界面参数(注意：history不需要重置)
fun init(
    firstStatus: MutableState<Int>,
    secondStatus: MutableState<Int>,
    gameStatus: MutableState<GameStatus>,
    cnt: MutableState<Int>
) {
    firstStatus.value = 0
    secondStatus.value = 0
    cnt.value = 0
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
    cnt: MutableState<Int>,
    curHistory: MutableList<String>
) {
    // 状态变量showDialog------与AlertDialog组件的显示与否进行绑定！！！
    var showDialog by remember { mutableStateOf(false) }
    // 配置本轮游戏的历史
    var history = "每次点数如下：\n"
    curHistory.forEach {
        history += it + "\n"
    }
    AlertDialog(
        onDismissRequest = { showDialog = false },
        title = { Text(title) },
        text = { Text(history) },
        confirmButton = {
            TextButton(
                onClick = {
                    // 确定按钮点击时执行的操作
                    showDialog = false
                    // 重置界面
                    init(firstStatus, secondStatus, gameStatus, cnt)
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
                    turnScreen(context, title, activityType)
                }
            ) {
                Text(text = "退出")
            }
        },
        modifier = Modifier.width(280.dp)
    )
}


fun <T> turnScreen(context: Context, result: String, activityType: Class<T>) {
    val intent = Intent(context, activityType)
    intent.putExtra("result", result)
    context.startActivity(intent)
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
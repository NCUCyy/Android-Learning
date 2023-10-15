package com.cyy.exp1.diceGame

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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
    // 骰子1的状态
    val firstStatus = remember { mutableStateOf(0) }
    // 骰子2的状态
    val secondStatus = remember { mutableStateOf(0) }
    // 游戏状态(初始化为：GameStatus.START)
    var gameStatus = remember { mutableStateOf(GameStatus.START) }

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
//                modifier = Modifier.height(400.dp)
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
                    // 抛骰子
                    firstStatus.value = game.rollDice()
                    secondStatus.value = game.rollDice()

                    // 第一次和后几次的逻辑不同
                    if (gameStatus.value == GameStatus.START) {
                        // 若是第一次抛骰子，则调用judgeFirstTurn()的逻辑进行判断
                        gameStatus.value =
                            game.judgeFirstTurn(firstStatus.value, secondStatus.value)
                    } else {
                        // 若已经抛过骰子，则调用judgeLaterTurn()的逻辑进行判断
                        gameStatus.value =
                            game.judgeLaterTurn(
                                firstStatus.value,
                                secondStatus.value,
                                gameStatus.value
                            )
                    }
                    // 判断游戏结果：
                    when (gameStatus.value) {
                        // 赢
                        GameStatus.WIN -> {
                            turnScreen(
                                context,
                                gameStatus.value.description,
                                GameWinActivity::class.java
                            )
                        }
                        // 输
                        GameStatus.LOSE -> {
                            turnScreen(
                                context,
                                gameStatus.value.description,
                                GameLoseActivity::class.java
                            )
                        }

                        else -> {
                            Toast.makeText(context, "请继续..", Toast.LENGTH_LONG).show()
                        }
                    }

                }) {
                    Text(text = "点击抛骰子")
                }
                Row {
                    Text(text = gameStatus.value.description, color = Color.White)
                }
            }
        }
    }
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
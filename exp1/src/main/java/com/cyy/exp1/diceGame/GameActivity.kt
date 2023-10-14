package com.cyy.exp1.diceGame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.cyy.exp1.Greeting
import com.cyy.exp1.R
import com.cyy.exp1.ui.theme.ExpTheme

class GameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExpTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GameScreen()
                }
            }
        }
    }
}

@Composable
fun GameScreen() {
    val context = LocalContext.current
    val game = DiceGame()
    val firstStatus = remember { mutableStateOf(0) }
    val secondStatus = remember { mutableStateOf(0) }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column {
            Row {
                if (firstStatus.value != 0 && secondStatus.value != 0) {
                    Image(
                        painter = painterResource(id = getResource(firstStatus.value)),
                        contentDescription = "骰子1"
                    )
                    Image(
                        painter = painterResource(id = getResource(secondStatus.value)),
                        contentDescription = "骰子2"
                    )
                }

            }
            Button(onClick = {
                firstStatus.value = game.rollDice()
                secondStatus.value = game.rollDice()
                var status = game.judgeGame(firstStatus.value, secondStatus.value)
                if (status == GameStatus.WIN) {
                    val intent = Intent(context, GameWinActivity::class.java)
                    context.startActivity(intent)
                } else if (status == GameStatus.LOSE) {
                    val intent = Intent(context, GameLoseActivity::class.java)
                    context.startActivity(intent)
                }

            }) {
                Text(text = "点击抛骰子")
            }
        }
    }
}

fun getResource(dicePoint: Int) = when (dicePoint) {
    1 -> R.mipmap.one
    2 -> R.mipmap.two
    3 -> R.mipmap.three
    4 -> R.mipmap.four
    5 -> R.mipmap.five
    6 -> R.mipmap.six
    else -> 0
}
package com.cyy.exp.ch02

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.integerArrayResource
import androidx.compose.ui.unit.sp
import com.cyy.exp.ui.theme.ExpTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExpTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    // 获取当前活动的上下文
    val context = LocalContext.current

//            Button(onClick = {
////                val intent = Intent(context, FirstActivity::class.java)
////                intent.putExtra("data", Teacher("cy", "female", 20))
////                context.startActivity(intent)
//                turnAction(
//                    context = context,
//                    activityType = FirstActivity::class.java,
//                    Teacher("cy", "female", 20)
//                )
//            }) {
//                Text("跳转到FirstActivity", fontSize = 30.sp)
//            }


//            Button(onClick = {
////                val intent = Intent(context, SecondActivity::class.java)
////                intent.putExtra("data", Student("002", "lsq", "male"))
////                context.startActivity(intent)
//                turnAction(
//                    context = context,
//                    activityType = SecondActivity::class.java,
//                    Student("002", "lsq", "male")
//                )
//            }) {
//                Text("跳转到SecondActivity", fontSize = 30.sp)
//            }
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Column {
            CustomButton(
                title = "跳转到FirstActivity",
                context = context,
                activityType = FirstActivity::class.java,
                data = Teacher("cy", "female", 20)
            )
            CustomButton(
                title = "跳转到SecondActivity",
                context = context,
                activityType = SecondActivity::class.java,
                data = Student("002", "lsq", "male")
            )
        }
    }
}

@Composable
fun <T, D : Parcelable> CustomButton(
    title: String,
    context: Context,
    activityType: Class<T>,
    data: D
) {
    Button(onClick = {
        turnAction(
            context = context,
            activityType = activityType,
            data = data
        )
    }) {
        Text(title, fontSize = 30.sp)
    }
}

fun <T, D : Parcelable> turnAction(context: Context, activityType: Class<T>, data: D) {
    val intent = Intent(context, activityType)
    intent.putExtra("data", data)
    context.startActivity(intent)
}

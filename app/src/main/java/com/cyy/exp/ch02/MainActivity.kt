package com.cyy.exp.ch02

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
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
        val resultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback {
                if (it.resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "返回MainActivity", Toast.LENGTH_LONG).show()
                }
            }
        )
        setContent {
            ExpTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(resultLauncher = resultLauncher)
                }
            }
        }
    }
}

@Composable
fun MainScreen(resultLauncher: ActivityResultLauncher<Intent>) {
    // 获取当前活动的上下文
    val context = LocalContext.current

    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Column {
            CustomButton(
                title = "跳转到FirstActivity",
                context = context,
                activityType = FirstActivity::class.java,
                data = Teacher("cy", "female", 20),
                resultLauncher = resultLauncher
            )
            CustomButton(
                title = "跳转到SecondActivity",
                context = context,
                activityType = SecondActivity::class.java,
                data = Student("002", "lsq", "male"),
                resultLauncher = resultLauncher
            )
        }
    }
}

@Composable
fun <T, D : Parcelable> CustomButton(
    title: String,
    context: Context,
    activityType: Class<T>,
    data: D,
    resultLauncher: ActivityResultLauncher<Intent>
) {
    Button(onClick = {
        turnAction(
            context = context,
            activityType = activityType,
            data = data,
            resultLauncher = resultLauncher
        )
    }) {
        Text(title, fontSize = 30.sp)
    }
}

fun <T, D : Parcelable> turnAction(
    context: Context,
    activityType: Class<T>,
    data: D,
    resultLauncher: ActivityResultLauncher<Intent>
) {
    val intent = Intent(context, activityType)
    intent.putExtra("data", data)
    resultLauncher.launch(intent)
}

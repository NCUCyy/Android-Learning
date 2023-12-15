package com.cyy.transapp.test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.drake.net.Get
import com.drake.net.utils.scopeNet

class TestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TestScreen()
        }
    }
}

@Composable
fun TestScreen() {
    // https://raw.githubusercontent.com/jason1105/Listening-to-English/main/VOA/voa-Everyday%20Grammar/2022-05-19%20Hobbies%20and%20Studies/%E6%96%B0%E5%BB%BA%E6%96%87%E6%9C%AC%E6%96%87%E6%A1%A3.txt
    // https://github.com/jason1105/Listening-to-English/blob/main/VOA/voa-Everyday%20Grammar/2022-05-19%20Hobbies%20and%20Studies/%E6%96%B0%E5%BB%BA%E6%96%87%E6%9C%AC%E6%96%87%E6%A1%A3.txt
    val txtState = remember { mutableStateOf("") }
    Column {
        Button(onClick = {
            scopeNet {
                // 若抛出异常，则sentenceModel不会被赋任何值
                txtState.value =
                    Get<String>("https://raw.githubusercontent.com/jason1105/Listening-to-English/main/VOA/voa-Everyday%20Grammar/2022-05-19%20Hobbies%20and%20Studies/%E6%96%B0%E5%BB%BA%E6%96%87%E6%9C%AC%E6%96%87%E6%A1%A3.txt") {
                    }.await()
            }
        }) {
            Text(text = "点击加载")
        }

        Text(text = txtState.value)
    }

}

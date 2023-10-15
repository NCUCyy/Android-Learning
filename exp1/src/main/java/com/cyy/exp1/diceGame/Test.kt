package com.cyy.exp1.diceGame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import com.cyy.exp1.R

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

class Test : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyList()
        }
    }
}


@Preview
@Composable
fun MyList() {
    val items = (1..20).toList() // 创建一个包含1到20的整数列表
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        for (i in 1..10) {
            ListItem(items[i])
        }
    }
}

@Composable
fun ListItem(item: Int) {
    // 这里可以定义每个列表项的内容
    Text(text = "Item $item", fontSize = 16.sp, color = Color.Black)
}

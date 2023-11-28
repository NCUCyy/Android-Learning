package com.cyy.exp2.psychological_test.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.cyy.exp2.psychological_test.pojo.Record

class ResultActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val record = intent.getParcelableExtra("record", Record::class.java)!!
        setContent {
            ResultScreen(record)
        }
    }
}

@Composable
fun ResultScreen(record: Record) {
    val context = LocalContext.current as Activity
    Button(onClick = {
        val intent = Intent()
        intent.putExtra("record", record)
        context.setResult(Activity.RESULT_OK, intent)
        context.finish()
    }) {
        Text(text = "返回")
    }
}
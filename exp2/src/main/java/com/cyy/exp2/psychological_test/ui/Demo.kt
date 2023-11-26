package com.cyy.exp2.psychological_test.ui

import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cyy.exp2.psychological_test.model.Data
import com.cyy.exp2.psychological_test.model.SentenceModel
import com.cyy.exp2.psychological_test.network.SerializationConverter
import com.cyy.exp2.psychological_test.view_model.SentenceViewModel
import com.cyy.exp2.psychological_test.view_model.UserViewModel
import com.cyy.exp2.psychological_test.view_model.UserViewModelFactory
import com.drake.net.Get
import com.drake.net.utils.scopeNet
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Preview
@Composable
fun DialogExample3() {
    var flag by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = { flag = true }) {
            Text("show Dialog")
        }
    }
    if (flag) {
        Card(elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp)) {
            Dialog(onDismissRequest = {
                flag = false
            }) {
                Box(
                    modifier = Modifier
                        .height(150.dp)
                        .width(300.dp)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Column {
                        LinearProgressIndicator()
                        Text("加载中 ing...")
                    }
                }


            }
        }
    }
}

@Preview
@Composable
fun LoadingDemo() {
    var flag by remember { mutableStateOf(false) }
    var showProgress by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = {
            flag = true
            coroutineScope.launch {
                delay(2000L) // 延迟2秒
                showProgress = false
            }
        }) {
            Text("show Dialog")
        }
    }
    if (flag && showProgress) {
        Dialog(onDismissRequest = {
            flag = false
        }) {
            Box(
                modifier = Modifier
                    .height(150.dp)
                    .width(300.dp)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Column {
                    LinearProgressIndicator()
                    Text("加载中 ing...")
                }
            }
        }
    }
}

class DemoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NetLibTest()
        }
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
@Preview
fun NetLibTest() {
    val sentenceViewModel = viewModel<SentenceViewModel>()
    val sentence = sentenceViewModel.sentence.collectAsState().value
    Column {
        TextButton(onClick = {
            sentenceViewModel.shuffleSentence()
        }) {
            Text(text = "刷新")
        }
        Text(text = sentence.data.en, fontSize = 20.sp)
        Text(text = sentence.data.zh, fontSize = 20.sp)
    }


}
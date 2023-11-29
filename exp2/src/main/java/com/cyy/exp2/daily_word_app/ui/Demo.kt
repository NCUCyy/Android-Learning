package com.cyy.exp2.daily_word_app.ui

import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.cyy.exp2.daily_word_app.view_model.SentenceViewModel
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownExample() {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("") }

    Box {
        Column {
            // Display selected option
            OutlinedTextField(
                value = selectedOption,
                onValueChange = {},
                label = { Text("选择题库") },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            expanded = true
                        }
                    )
                },
                readOnly = true
            )
            // Dropdown menu
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                // Dropdown items
                DropdownMenuItem(onClick = {
                    selectedOption = "Option 1"
                    expanded = false
                }, text = {
                    Text("Option 1")
                })
                DropdownMenuItem(onClick = {
                    selectedOption = "Option 2"
                    expanded = false
                }, text = {
                    Text("Option 2")
                })
                // Add more items as needed
            }
        }
    }
}

@Preview
@Composable
fun DropdownExamplePreview() {
    DropdownExample()
}
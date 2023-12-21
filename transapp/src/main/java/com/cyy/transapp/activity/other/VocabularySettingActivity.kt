package com.cyy.transapp.activity.other

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cyy.transapp.R
import com.cyy.transapp.TransApp
import com.cyy.transapp.model.Vocabulary
import com.cyy.transapp.view_model.vocabulary.VocabularySettingViewModel
import com.cyy.transapp.view_model.vocabulary.VocabularySettingViewModelFactory

class VocabularySettingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userId = intent.getIntExtra("userId", 20)
        val vocabulary = intent.getStringExtra("vocabulary")!!
        setContent {
            VocabularySettingMainScreen(userId, vocabulary)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularySettingMainScreen(userId: Int, vocabulary: String) {
    val context = LocalContext.current as Activity
    val application = LocalContext.current.applicationContext as TransApp
    val vocabularySettingViewModel = viewModel<VocabularySettingViewModel>(
        factory = VocabularySettingViewModelFactory(
            userId,
            vocabulary,
            context,
            application.userRepository,
            application.planRepository,
            application.vocabularyRepository
        )
    )
    val plan = vocabularySettingViewModel.curPlanState.value.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // TODO：显示查询的词汇
                    Text(
                        text = "切换到单词本",
                        fontWeight = FontWeight.Bold,
                        fontSize = 25.sp,
                        maxLines = 1
                    )
                },
                // 左侧图标
                navigationIcon = {
                    // 图标按钮
                    IconButton(onClick = {
                        // TODO：返回
                        val intent = Intent()
                        context.setResult(
                            Activity.RESULT_OK,
                            intent.putExtra("vocabulary", plan.value.vocabulary)
                        )
                        context.finish()
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.back),
                            contentDescription = null,
                        )
                    }
                },
                actions = {
                }
            )
        },
        bottomBar = {
        },
        content = {
            // 页面的主体部分
            Box(modifier = Modifier.padding(it)) {
                VocabularySettingScreen(vocabularySettingViewModel)
            }
        },
        floatingActionButton = {
        })
}

@Composable
fun VocabularySettingScreen(vocabularySettingViewModel: VocabularySettingViewModel) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 10.dp, end = 10.dp)
            .verticalScroll(scrollState)
    ) {
        SelectDailyNumCard(vocabularySettingViewModel)
        vocabularySettingViewModel.getAllVocabulary().forEach { vocabulary ->
            VocabularySettingItem(vocabulary, vocabularySettingViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectDailyNumCard(vocabularySettingViewModel: VocabularySettingViewModel) {
    val plan = vocabularySettingViewModel.curPlanState.value.collectAsStateWithLifecycle()
    val expanded = remember { mutableStateOf(false) }
    val options = listOf(10, 20, 30, 50, 100, 200)
    val selectedOptionText = remember { mutableStateOf(plan.value.dailyNum) }
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded.value,
                onExpandedChange = {
                    expanded.value = !expanded.value
                }
            ) {
                TextField(
                    readOnly = true,
                    value = plan.value.dailyNum.toString(),
                    onValueChange = {},
                    label = { Text(text = "每组单词数量") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expanded.value
                        )
                    },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(

                    ),
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded.value,
                    onDismissRequest = {
                        expanded.value = false
                    },
                ) {
                    options.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = {
                                Text(selectionOption.toString())
                            },
                            onClick = {
                                selectedOptionText.value = selectionOption
                                vocabularySettingViewModel.updateDailyNum(selectionOption)
                                expanded.value = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VocabularySettingItem(
    vocabulary: Vocabulary,
    vocabularySettingViewModel: VocabularySettingViewModel
) {
    val curPlanState =
        vocabularySettingViewModel.curPlanState.value.collectAsStateWithLifecycle()
    val isSelected = remember {
        mutableStateOf(false)
    }
    isSelected.value = curPlanState.value.vocabulary == vocabulary.desc
    Card(
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = vocabulary.desc,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
            if (isSelected.value) {
                IsSelectCard()
            } else {
                NotSelectedCard(vocabulary, vocabularySettingViewModel)
            }
        }
    }
}

@Composable
fun NotSelectedCard(
    vocabulary: Vocabulary,
    vocabularySettingViewModel: VocabularySettingViewModel
) {
    Card(
        modifier = Modifier
            .clickable {
                // TODO：切换单词本
                vocabularySettingViewModel.updateVocabulary(vocabulary)
            }
            .fillMaxWidth()
            .padding(start = 250.dp)
    ) {
        Text(
            text = "切换",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF75994B),
            modifier = Modifier.padding(10.dp)
        )
    }
}

@Composable
fun IsSelectCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 250.dp),
    ) {
        Text(
            text = "当前单词本",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            modifier = Modifier.padding(10.dp)
        )
    }
}
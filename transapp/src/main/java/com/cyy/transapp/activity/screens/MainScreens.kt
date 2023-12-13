package com.cyy.transapp.activity.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cyy.transapp.R
import com.cyy.transapp.TransApp
import com.cyy.transapp.view_model.QueryViewModel
import com.cyy.transapp.view_model.QueryViewModelFactory

val screens = listOf(Screen.QueryPage, Screen.ListenPage, Screen.LearnPage)

/**
 *Screen类（与用于显示的Screen实体不同！要区分开！Screen类只用于提供页面需要的元数据metaData：icon、title、"route"【用于导航】）
 */
sealed class Screen(val route: String, val title: String, val icon: Int) {
    object QueryPage :
        Screen(route = "query", title = "查词", icon = R.drawable.trans)

    object ListenPage :
        Screen(route = "listen", title = "听力", icon = R.drawable.listen)

    object LearnPage :
        Screen(route = "learn", title = "学习", icon = R.drawable.dictionary)
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun QueryScreen() {
    val application = LocalContext.current.applicationContext as TransApp
    val queryViewModel = viewModel<QueryViewModel>(
        factory = QueryViewModelFactory(
            application.queryRepository,
        )
    )
    val query = queryViewModel.query.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        // 1、输入框
        Card(elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp)) {
            TextField(
                value = query.value,
                modifier = Modifier
                    .fillMaxWidth(),
                onValueChange = { it: String ->
                    queryViewModel.updateQuery(it)
                },
                placeholder = { Text(text = "查询单词或句子") },
                shape = MaterialTheme.shapes.extraSmall, // 设置边框形状
                textStyle = TextStyle.Default.copy(color = Color.Black), // 设置文本颜色
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    placeholderColor = Color.Gray,
                ), keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(onSearch = {
                    // TODO:跳转到TransActivity
                })
            )
        }
        // 2、每日一句
        // 3、查词历史
    }
}


@Composable
fun ListenScreen() {

}

@Composable
fun LearnScreen() {

}

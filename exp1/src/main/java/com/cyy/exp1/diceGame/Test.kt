import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun MyTableWithSearch() {
    var query by remember { mutableStateOf("") }
    val items = listOf(
        "Item 1",
        "Item 2",
        "Item 3",
        "Item 4",
        "Item 5"
    )

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        BasicTextField(
            value = query,
            onValueChange = { newValue ->
                query = newValue
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    // 处理查询操作
                    // 在这里，你可以根据输入框的值筛选表格中的项
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        // 表格
        Table(
            query = query,
            items = items,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun Table(query: String, items: List<String>, modifier: Modifier) {
    val filteredItems = items.filter { it.contains(query, ignoreCase = true) }

    Column(
        modifier = modifier
    ) {
        filteredItems.forEach { item ->
            ListItem(item)
        }
    }
}

@Composable
fun ListItem(item: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(Color.Gray),
        contentAlignment = Alignment.Center
    ) {
        Text(text = item, color = Color.White)
    }
}

@Preview
@Composable
fun MyTableWithSearchPreview() {
    MyTableWithSearch()
}

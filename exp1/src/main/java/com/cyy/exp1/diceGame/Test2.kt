import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun StylishList(data: List<String>) {
    Column {
        data.forEach {
            MyListItem(item = it.toString())
        }
    }
}

@Composable
fun MyListItem(item: String) {
    var textState by remember { mutableStateOf(item) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.White)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = textState)
        }
        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .align(Alignment.CenterEnd)
                .padding(8.dp)
        )
    }
}

@Preview
@Composable
fun PreviewStylishList() {
    val data = remember {
        listOf(
            "Item 1",
            "Item 2",
            "Item 3",
            "Item 4",
            "Item 5"
        )
    }

    StylishList(data)
}
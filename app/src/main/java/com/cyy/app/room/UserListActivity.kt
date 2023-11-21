package com.cyy.app.room

import android.app.Application
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cyy.app.room.Record
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class UserListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ContentView()
        }
    }
}

@Composable
fun ContentView() {
    val application = LocalContext.current.applicationContext as MyApp
    val userViewModel = viewModel<UserViewModel>(
        factory = GenericViewModelFactory(
            application.userRepository,
            UserViewModel::class.java
        )
    )
    val mode by userViewModel.mode.collectAsState()
    val loginUser by userViewModel.loginUser.collectAsState()
    val loginRes by userViewModel.loginRes.collectAsState()
    val registerRes by userViewModel.registerRes.collectAsState()
    var recordViewModel: RecordViewModel? = null
    if (loginRes) {
        recordViewModel = viewModel(
            factory = RecordViewModelRepository(
                application.recordRepository,
                userViewModel.loginUser.value?.id ?: -1
            )
        )
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(255, 204, 0),
                contentColor = Color.Black
            )
        ) {
            Text(text = loginUser.toString())
        }
        Row {
            Button(onClick = {
                // 查询不到返回null
                userViewModel.login("cyy2", "cyy")
            }) {
                Text(text = "登录")
            }
            Button(onClick = {
                userViewModel.register(User("cyy10", "cyy", "male"))
            }) {
                Text(text = "添加User")
            }
            Button(onClick = {
                recordViewModel!!.insert(Record(OffsetDateTime.now(), 100, loginUser!!.id))
            }) {
                Text(text = "添加Record")
            }
            Button(onClick = { userViewModel.deleteAll() }) {
                Text(text = "删除所有")
            }
        }
        if (loginRes) {
            val records = recordViewModel!!.records.collectAsStateWithLifecycle()
            LazyColumn {
                items(records.value) {
                    Row {
                        Text(text = it.testTme.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    }
                }
            }
        }
    }
}

//@Composable
//fun UserList(modifier: Modifier = Modifier, userList: List<User>) {
//    LazyColumn(
//        modifier
//            .fillMaxWidth()
//            .background(Color.Gray),
//        contentPadding = PaddingValues(15.dp),
//        verticalArrangement = Arrangement.spacedBy(8.dp)
//    ) {
//        items(userList) { user ->
//            CardContent(user.toString())
//        }
//    }
//}
//
//@Composable
//fun CardContent(text: String) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(5.dp),
//        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
//        colors = CardDefaults.cardColors(
//            containerColor = Color(255, 204, 0),
//            contentColor = Color.Black
//        )
//    ) {
//        Text(text = text)
//    }
//}



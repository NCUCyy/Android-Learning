package com.cyy.app.room

import android.os.Bundle
import android.util.Log
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

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
        factory = GenericViewModelFactory((application).repository, UserViewModel::class.java)
    )
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        val userList by userViewModel.allUser.collectAsStateWithLifecycle()
        UserList(Modifier.weight(1f), userList)
        Row {
            Button(onClick = {
                val user = User("cyy", "cyy", "男")
                userViewModel.insert(user)
            }) {
                Text(text = "新增")
            }
            Button(onClick = { userViewModel.deleteAll() }) {
                Text(text = "删除所有")
            }
        }
    }
}

@Composable
fun UserList(modifier: Modifier = Modifier, userList: List<User>) {
    LazyColumn(
        modifier
            .fillMaxWidth()
            .background(Color.Gray),
        contentPadding = PaddingValues(15.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(userList) { user ->
            CardContent(user.toString())
        }
    }
}

@Composable
fun CardContent(text: String) {
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
        Text(text = text)
    }
}



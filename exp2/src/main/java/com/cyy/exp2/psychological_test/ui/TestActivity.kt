package com.cyy.exp2.psychological_test.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.TextToolbar
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cyy.exp2.psychological_test.PsychologicalTestApp
import com.cyy.exp2.psychological_test.pojo.Record
import com.cyy.exp2.psychological_test.pojo.User
import com.cyy.exp2.psychological_test.view_model.RecordViewModel
import com.cyy.exp2.psychological_test.view_model.RecordViewModelFactory
import com.cyy.exp2.psychological_test.view_model.UserViewModel
import com.cyy.exp2.psychological_test.view_model.UserViewModelFactory
import java.time.OffsetDateTime

class TestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val userId = intent.getIntExtra("userId", -1)
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen(userId)
        }
    }
}

val screens = listOf(Screen.TestPage, Screen.HistoryPage, Screen.UserPage)

/**
 *Screen类（与用于显示的Screen实体不同！要区分开！Screen类只用于提供页面需要的元数据metaData：icon、title、"route"【用于导航】）
 */
sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object TestPage :
        Screen(route = "testing", title = "答题", icon = Icons.Filled.Edit)

    object HistoryPage :
        Screen(route = "testHistory", title = "答题历史", icon = Icons.Filled.List)

    object UserPage :
        Screen(route = "user", title = "个人信息", icon = Icons.Filled.AccountCircle)
}

@Composable
fun MainScreen(userId: Int) {
    val context = LocalContext.current
    val application = LocalContext.current.applicationContext as PsychologicalTestApp
    val recordViewModel = viewModel<RecordViewModel>(
        factory = RecordViewModelFactory(
            application.recordRepository,
            application.userRepository,
            userId
        )
    )
    val records = recordViewModel.records.collectAsStateWithLifecycle()
    val loginUser = recordViewModel.loginUser.collectAsStateWithLifecycle()
    // 测试跳转是否成功------------已成功
    demo(recordViewModel, loginUser, records, userId)
}

@Composable
fun demo(
    recordViewModel: RecordViewModel,
    loginUser: State<User>,
    records: State<List<Record>>,
    userId: Int
) {
    Column {
        Text(text = "欢迎回来，${loginUser.value}!")
        Button(onClick = {
            recordViewModel.insert(Record(OffsetDateTime.now(), 10, userId))
        }) {
            Text(text = "添加Record")
        }
        LazyColumn {
            items(records.value) {
                Row {
                    Text(text = it.toString())
                }
            }
        }

    }
}
package com.cyy.exp2.psychological_test.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cyy.exp2.psychological_test.PsychologicalTestApp
import com.cyy.exp2.psychological_test.pojo.Record
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
            TestScreen(userId)
        }
    }
}

@Composable
fun TestScreen(userId: Int) {
    val context = LocalContext.current
    val application = LocalContext.current.applicationContext as PsychologicalTestApp
    val recordViewModel = viewModel<RecordViewModel>(
        factory = RecordViewModelFactory(
            application.recordRepository,
            userId
        )
    )
    val records = recordViewModel.records.collectAsStateWithLifecycle()
    demo(recordViewModel, records, userId)
}

@Composable
fun demo(recordViewModel: RecordViewModel, records: State<List<Record>>, userId: Int) {
    Column {
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
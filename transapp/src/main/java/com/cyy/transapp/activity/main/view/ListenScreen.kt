package com.cyy.transapp.activity.main.view

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cyy.transapp.TransApp
import com.cyy.transapp.activity.main.ListenActivity
import com.cyy.transapp.activity.main.StateHolder
import com.cyy.transapp.pojo.ListenResource
import com.cyy.transapp.util.FileUtil
import com.cyy.transapp.view_model.ListenViewModel
import com.cyy.transapp.view_model.ListenViewModelFactory


// ----------------------------------------------------②ListenScreen----------------------------------------------------
@Composable
fun ListenScreen(states: StateHolder) {
    val application = LocalContext.current.applicationContext as TransApp
    val listenViewModel =
        viewModel<ListenViewModel>(factory = ListenViewModelFactory(application.listenRepository))
    val listenResources = listenViewModel.getALlListenResource()
    LazyColumn {
        items(listenResources) { listenResource ->
            ListenResourceCard(listenResource, states)
        }
    }
}

@Composable
fun ListenResourceCard(listenResource: ListenResource, states: StateHolder) {
    val context = LocalContext.current as Activity
    // 从raw读取txt
    val en = FileUtil.readRawToTxt(context, listenResource.en)
    val zh = FileUtil.readRawToTxt(context, listenResource.zh)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable {
                // TODO：跳转到ListenActivity
                val intent = Intent(context, ListenActivity::class.java)
                intent.putExtra("listenResource", listenResource)
                states.resultLauncher.launch(intent)
            },
        shape = RoundedCornerShape(5.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFEBF4FA),
            contentColor = Color.Black
        ), elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp)
    ) {
        Row {
            Image(
                painter = painterResource(id = listenResource.img),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .padding(start = 10.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp)
            ) {
                Text(
                    text = listenResource.topic,
                    fontSize = 23.sp,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF2196F3),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(10.dp)
                )
                Text(
                    text = en,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                    maxLines = 2,
                    color = Color.Gray
                )
            }
        }
    }
}

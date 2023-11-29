package com.cyy.exp2.psychological_test.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.CpuUsageInfo
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.cyy.exp2.R
import com.cyy.exp2.psychological_test.pojo.Record
import java.time.OffsetDateTime

class ResultActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val record = intent.getParcelableExtra("record", Record::class.java)!!
        val username = intent.getStringExtra("username")!!
        setContent {
            ResultScreen(record, username)
        }
    }
}

@Composable
fun TestJump(record: Record) {
    val context = LocalContext.current as Activity
    Button(onClick = {
        val intent = Intent()
        intent.putExtra("record", record)
        context.setResult(Activity.RESULT_OK, intent)
        context.finish()
    }) {
        Text(text = "返回")
    }
}

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    record: Record = Record(OffsetDateTime.now(), "CET-4", 3, 10, 5, "0"),
    username: String = "cyy"
) {
    val context = LocalContext.current as Activity
    Scaffold(
        //定义头部
        topBar = {
            // 定义顶部栏需要解决两个问题：
            // （1）需要在顶部栏定义顶部的右侧导航菜单
            // （2）需要定义顶部的导航按钮，使得启动侧滑菜单
            TopAppBar(
                title = {
                },
                // 左侧图标
                navigationIcon = {
                    IconButton(onClick = {
                        // TODO：返回MainActivity
                        val intent = Intent()
                        intent.putExtra("record", record)
                        context.setResult(Activity.RESULT_OK, intent)
                        context.finish()
                    }) {
                        Icon(Icons.Filled.KeyboardArrowLeft, contentDescription = null)
                    }
                },
                // 右侧按钮————按行处理的交互
                actions = {
                    IconButton(onClick = {
                        // TODO：分享结果

                    }) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = "Localized description"
                        )
                    }
                }
            )
        },
        content = {
            // 页面的主体部分
            Box(modifier = Modifier.padding(it)) {
                MainResultScreen(record, username)
            }
        })
}

@Composable
@Preview
fun MainResultScreen(
    record: Record = Record(OffsetDateTime.now(), "CET-4", 3, 10, 5, "0"),
    username: String = "cyy"
) {
    val context = LocalContext.current as Activity
    val totalCnt: Int = record.right + record.wrong + record.undo
    Column(
        modifier = Modifier.size(width = 400.dp, height = 600.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "恭喜你完成答题🎉",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(10.dp))
        Card(
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White,
            ),
            modifier = Modifier
                .padding(10.dp)
                .size(width = 400.dp, height = 400.dp),
        ) {
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = "题库：${record.category}",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(18.dp)
            )
            Divider(
                modifier = Modifier
                    .fillMaxWidth(),
                color = Color(0xFFC4C4C4),
            )
            Spacer(modifier = Modifier.height(40.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Spacer(modifier = Modifier.width(10.dp))
                Image(
                    painter = painterResource(id = android.R.mipmap.sym_def_app_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "$username",
                    fontSize = 27.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 13.dp)
                )
                Spacer(modifier = Modifier.width(120.dp))
                Text(
                    text = "${record.right}/$totalCnt",
                    fontSize = 35.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 25.dp, top = 15.dp)
                )
            }
            Spacer(modifier = Modifier.height(55.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = record.duration,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4FC6FC),
                        fontSize = 25.sp
                    )
                    Text(text = "用时", color = Color.Gray, fontSize = 25.sp)
                }
                Column(
                    modifier = Modifier
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = record.wrong.toString(),
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE90C57),
                        fontSize = 25.sp
                    )
                    Text(text = "错题数", color = Color.Gray, fontSize = 25.sp)
                }
                Column(
                    modifier = Modifier
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${
                            String.format(
                                "%.2f",
                                ((record.right + 0.0) / totalCnt) * 100
                            )
                        }%",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4FC6FC),
                        fontSize = 25.sp
                    )
                    Text(text = "正确率", color = Color.Gray, fontSize = 25.sp)
                }
            }
        }
        CustomButton(text = "分享结果", onClick = {
            // TODO：分享结果
        }, containerColor = Color(0xFFA7FFAA), icon = Icons.Filled.Share)
        Spacer(modifier = Modifier.padding(5.dp))
        CustomButton(text = "返回首页", onClick = {
            // TODO：直接返回首页！！
            val intent = Intent()
            intent.putExtra("record", record)
            context.setResult(Activity.RESULT_OK, intent)
            context.finish()
        }, containerColor = Color(0xFF96DEFF), icon = Icons.Filled.Home)
    }
}

@Composable
fun CustomButton(
    text: String,
    onClick: () -> Unit,
    containerColor: Color,
    icon: ImageVector
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = Color.Black
        )
    ) {
        Text(text = text, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(ButtonDefaults.IconSize)
        )
    }

}

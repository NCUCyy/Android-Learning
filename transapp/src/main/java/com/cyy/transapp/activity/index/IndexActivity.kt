package com.cyy.transapp.activity.index

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cyy.transapp.R

class IndexActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 使用ActivityResultLauncher进行意图跳转
        val resultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            // 意图结束后，执行这个「回调函数」
            ActivityResultCallback {
            }
        )
        setContent {
            IndexScreen(resultLauncher)
        }
    }
}

@Composable
fun IndexScreen(resultLauncher: ActivityResultLauncher<Intent>) {
    val context = LocalContext.current as Activity
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(120.dp))
        Text(
            text = "Welcome",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            fontSize = 50.sp,
        )
        Text(
            text = "to Trans",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            fontSize = 50.sp
        )
        Spacer(modifier = Modifier.height(25.dp))
        Text(
            text = "Lets start here!",
            style = MaterialTheme.typography.titleSmall,
            fontSize = 15.sp
        )
        Spacer(modifier = Modifier.height(50.dp))
        Button(
            elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
            onClick = {
                // TODO：跳转到LoginActivity
                val intent = Intent(context, LoginActivity::class.java)
                resultLauncher.launch(intent)
            }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Get Started",
                    fontSize = 17.sp,
                    modifier = Modifier.padding(
                        start = 10.dp,
                        end = 10.dp,
                        top = 20.dp,
                        bottom = 20.dp
                    ),
                )
                Card(
                    shape = RoundedCornerShape(7.dp, 7.dp, 7.dp, 7.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF302F2F))
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.next),
                        contentDescription = null,
                        modifier = Modifier.padding(5.dp)
                    )
                }
            }
        }
    }
}
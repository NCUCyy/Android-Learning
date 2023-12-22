package com.cyy.transapp.test

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.cyy.transapp.R
import com.drake.net.Get
import com.drake.net.utils.scopeNet

class TestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TestScreen2()
        }
    }
}

@Composable
fun TestScreen() {
    // https://raw.githubusercontent.com/jason1105/Listening-to-English/main/VOA/voa-Everyday%20Grammar/2022-05-19%20Hobbies%20and%20Studies/%E6%96%B0%E5%BB%BA%E6%96%87%E6%9C%AC%E6%96%87%E6%A1%A3.txt
    // https://github.com/jason1105/Listening-to-English/blob/main/VOA/voa-Everyday%20Grammar/2022-05-19%20Hobbies%20and%20Studies/%E6%96%B0%E5%BB%BA%E6%96%87%E6%9C%AC%E6%96%87%E6%A1%A3.txt
    val txtState = remember { mutableStateOf("") }
    Column {
        Button(onClick = {
            scopeNet {
                // 若抛出异常，则sentenceModel不会被赋任何值
                txtState.value =
                    Get<String>("https://raw.githubusercontent.com/jason1105/Listening-to-English/main/VOA/voa-Everyday%20Grammar/2022-05-19%20Hobbies%20and%20Studies/%E6%96%B0%E5%BB%BA%E6%96%87%E6%9C%AC%E6%96%87%E6%A1%A3.txt") {
                    }.await()
            }
        }) {
            Text(text = "点击加载")
        }

        Text(text = txtState.value)
    }

}

@Composable
fun TestScreen2() {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                imageUri = it
            }
        }
    )
    val rainbowColorsBrush = remember {
        Brush.sweepGradient(
            listOf(
                Color(0xFF9575CD),
                Color(0xFFBA68C8),
                Color(0xFFE57373),
                Color(0xFFFFB74D),
                Color(0xFFFFF176),
                Color(0xFFAED581),
                Color(0xFF4DD0E1),
                Color(0xFF9575CD)
            )
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.user),
            contentDescription = null,
            modifier = Modifier
                .size(36.dp),
        )
        imageUri?.let {
            Image(
                painter = rememberAsyncImagePainter(model = imageUri),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .border(
                        BorderStroke(4.dp, rainbowColorsBrush),
                        CircleShape
                    )
                    .size(400.dp)
                    .clip(CircleShape)
            )
        }

        TextButton(
            onClick = {
                galleryLauncher.launch("image/*")
            }
        ) {
            Text(
                text = "选择头像"
            )
        }
    }
}
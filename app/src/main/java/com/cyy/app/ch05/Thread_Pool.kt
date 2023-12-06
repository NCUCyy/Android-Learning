package com.cyy.app.ch05

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.HandlerCompat
import androidx.lifecycle.ViewModel
import coil.compose.AsyncImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.Executor

sealed class Result<out R> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}

/**
 * 定义图形仓库
 * @property executor Executor
 * @property resultHandler Handler
 * @property imageLst List<String>
 * @constructor
 */
class ImageRepository(
    private val executor: Executor,
    private val resultHandler: Handler
) {
    val imageLst: List<String> = listOf(
        "https://img0.baidu.com/it/u=3368678403,249914024&fm=253&fmt=auto&app=138&f=JPEG?w=889&h=500",
        "https://img1.baidu.com/it/u=1586503404,2024787974&fm=253&app=120&size=w931&n=0&f=JPEG&fmt=auto?sec=1701882000&t=d5aac50f5ea61054720081dd478a7939",
        "https://img0.baidu.com/it/u=678433132,1708154179&fm=253&app=138&size=w931&n=0&f=JPEG&fmt=auto?sec=1701882000&t=efaa22401649b017f82e9666a6cd72bb",
    )


    /**
     * 请求加载数据
     * @param imageId Int 在图片列表中的序号
     * @param callBack Function1<Result<String>, Unit> 回调
     */
    fun loadImageById(
        imageId: Int,
        callBack: (Result<String>) -> Unit
    ) {
        //线程池中创建一个新线程并执行
        executor.execute {
            try {
                //按照列表索引请求图片资源
                val successResult = loadSynImageById(imageId)
                //与主线程通信
                resultHandler.post {
                    callBack(successResult)
                }
            } catch (e: Exception) {
                val errorResult = Result.Error(e)
                resultHandler.post {
                    callBack(errorResult)
                }
            }
        }
    }

    private fun loadSynImageById(imageId: Int): Result<String> {
        if (imageId >= imageLst.size || imageId < 0)
            return Result.Error(Exception("图片索引存在问题"))
        return Result.Success(imageLst[imageId])
    }
}

class LoadImageViewModel(private val imageRepository: ImageRepository) : ViewModel() {
    private val _currentImageId = MutableStateFlow<Int>(0)
    val currentImageId = _currentImageId.asStateFlow()

    private val _currentImage = MutableStateFlow<String>("")
    val currentImage = _currentImage.asStateFlow()

    /**
     * Change image index
     * 修改图片索引
     */
    fun changeImageIndex() {
        _currentImageId.value = (_currentImageId.value + 1) % imageRepository.imageLst.size
    }

    /**
     * Request image
     * 请求图片
     */
    fun requestImage() {
        imageRepository.loadImageById(_currentImageId.value) { it: Result<String> ->
            when (it) {
                //获取成功，修改在线图片的url
                is Result.Success<String> -> _currentImage.value = it.data
                //获取失败，提供失败的描述信息
                else -> _currentImage.value = "加载在线图片资源失败"
            }
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            //创建应用对象
            val application = application as MyApplication
            //创建处理器
            val handler = HandlerCompat.createAsync(Looper.getMainLooper())
            //创建图形仓库
            val repository = ImageRepository(
                executor = application.threadPoolExecutor,
                resultHandler = handler
            )

            val imageViewModel = LoadImageViewModel(repository)

            ImageScreen(imageViewModel = imageViewModel)
        }
    }
}


@Composable
fun ImageScreen(imageViewModel: LoadImageViewModel) {
    //获取当前图片状态
    val imageURLState = imageViewModel.currentImage.collectAsState()
    //获取当前图片索引
    val imageIdState = imageViewModel.currentImageId.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (imageURLState.value.isNotBlank()) {
                AsyncImage(
                    modifier = Modifier
                        .width(400.dp)
                        .height(400.dp)
                        .border(BorderStroke(1.dp, Color.Blue)),
                    model = imageURLState.value,
                    contentDescription = null
                )
            } else
                Text(
                    modifier = Modifier
                        .width(300.dp)
                        .height(500.dp),
                    text = "等待加载图片",
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    color = Color.White
                )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 10.dp), horizontalArrangement = Arrangement.Center
            ) {
                TextButton(colors = ButtonDefaults.buttonColors(
                    contentColor = Color.Blue,
                    containerColor = Color.LightGray
                ),
                    onClick = {
                        //请求图片
                        imageViewModel.requestImage()
                        //修改索引
                        imageViewModel.changeImageIndex()
                    }) {
                    Text("动态显示图片", fontSize = 16.sp)
                }
            }
        }
    }
}

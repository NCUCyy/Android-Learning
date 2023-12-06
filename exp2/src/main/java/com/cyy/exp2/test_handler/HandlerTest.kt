package com.cyy.exp2.test_handler


import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
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
import com.cyy.exp2.daily_word_app.model.SentenceModel
import com.cyy.exp2.daily_word_app.network.SerializationConverter
import com.drake.net.Get
import com.drake.net.utils.scopeNet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.Executor

sealed class Result<out R> {
    // 成功时，返回图片的URL
    data class Success<out T>(val data: T) : Result<T>()
    // 错误时，返回报错信息errorDesc
    data class Error<out T>(val errorDesc: T) : Result<T>()
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
     * @param callBack Function1<Result<String>, Unit> 回调
     */
    fun loadImage(
        callBack: (Result<String>) -> Unit
    ) {
        var sentenceModel: SentenceModel
        //线程池中创建一个新线程并执行
        executor.execute {

            // 开启协程
            scopeNet {
                // 若抛出异常，则sentenceModel不会被赋任何值
                sentenceModel = Get<SentenceModel>("https://api.vvhan.com/api/en?type=sj") {
                    converter = SerializationConverter()
                }.await()
                // TODO：成功！按照列表索引请求图片资源~
                val successResult = Result.Success(sentenceModel.data.pic)
                // 与主线程通信
                resultHandler.post {
                    callBack(successResult)
                }
            }.catch {
                it
                // TODO：失败！显示错误信息~
                val errorResult = Result.Error("加载在线图片失败！")
                // 与主线程通信
                resultHandler.post {
                    callBack(errorResult)
                }
            }
        }
    }
}

class LoadImageViewModel(private val imageRepository: ImageRepository) : ViewModel() {
    private val _currentImageURL = MutableStateFlow("")
    val currentImageURL = _currentImageURL.asStateFlow()

    /**
     * Request image
     * 请求图片
     */
    fun requestImage() {
        // TODO：在ViewModel中修改页面的状态值
        // 修改页面的显示文本为"正在加载"
        _currentImageURL.value = "等待加载图片~"
        imageRepository.loadImage { it: Result<String> ->
            when (it) {
                //获取成功，修改在线图片的url
                is Result.Success<String> -> _currentImageURL.value = it.data
                //获取失败，提供失败的描述信息
                is Result.Error<String> -> _currentImageURL.value = it.errorDesc
            }
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // TODO：注册Handler，传入Repository
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
    // 获取当前图片状态————由ViewModel管理状态值的变化
    val imageURLState = imageViewModel.currentImageURL.collectAsState()

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
            // TODO：动态显示请求结果（正在：加载中/成功：URL/失败：加载在线图片资源失败）
            Text(imageURLState.value, fontSize = 20.sp, color = Color.White)
            if (imageURLState.value.isNotBlank()) {
                AsyncImage(
                    modifier = Modifier
                        .width(400.dp)
                        .height(400.dp)
                        .border(BorderStroke(1.dp, Color.Blue)),
                    model = imageURLState.value,
                    contentDescription = null
                )
            }
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
                        // TODO：请求图片
                        imageViewModel.requestImage()
                    }) {
                    Text("动态显示图片", fontSize = 16.sp)
                }
            }
        }
    }
}

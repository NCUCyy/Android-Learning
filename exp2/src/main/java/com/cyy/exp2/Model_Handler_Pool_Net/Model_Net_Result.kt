package com.cyy.exp2.Model_Handler_Pool_Net


import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

sealed class Result2<out R> {
    object NotBegin : Result2<Nothing>()

    // 成功时，返回图片的URL
    data class Success<out T>(val data: T) : Result2<T>()

    // 错误时，返回报错信息errorDesc
    data class Error<out T>(val errorDesc: T) : Result2<T>()
    object Loading : Result2<Nothing>()
}

/**
 * 定义图形仓库
 * @constructor
 */
class ImageRepository2(
) {
    /**
     * 请求加载数据
     * @param callBack Function1<Result2<String>, Unit> 回调
     */
    fun loadImage(
        callBack: (Result2<String>) -> Unit
    ) {
        var sentenceModel: SentenceModel
        //线程池中创建一个新线程并执行
        // 开启协程
        scopeNet {
            // 若抛出异常，则sentenceModel不会被赋任何值
            sentenceModel = Get<SentenceModel>("https://api.vvhan.com/api/en?type=sj") {
                converter = SerializationConverter()
            }.await()
            // TODO：成功！按照列表索引请求图片资源~
            val successResult2 = Result2.Success(sentenceModel.data.pic)
            // 与主线程通信
            callBack(successResult2)
        }.catch {
            it
            // TODO：失败！显示错误信息~
            val errorResult2 = Result2.Error("加载在线图片失败！")
            callBack(errorResult2)
        }
    }
}

class LoadImageViewModel2(private val imageRepository: ImageRepository2) : ViewModel() {
    // 图片的请求结果（4个：未请求、请求中、请求成功、请求失败）
    private val _requestState = MutableStateFlow<Result2<Any>>(Result2.NotBegin)
    val requestState = _requestState.asStateFlow()

    /**
     * Request image
     * 请求图片
     */
    fun requestImage() {
        // TODO：在ViewModel中修改页面的状态值
        _requestState.value = Result2.Loading
        imageRepository.loadImage { it: Result2<String> ->
            when (it) {
                //获取成功，修改在线图片的url
                is Result2.Success<String> -> {
                    _requestState.value = Result2.Success(it.data)
                }
                //获取失败，提供失败的描述信息
                is Result2.Error<String> -> _requestState.value = Result2.Error(it.errorDesc)
                else -> {
                    _requestState.value = Result2.Error("加载在线图片失败！")
                }
            }
        }
    }
}

class MainActivity2 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            //创建图形仓库
            val repository = ImageRepository2()
            val imageViewModel = LoadImageViewModel2(repository)
            ImageScreen2(imageViewModel = imageViewModel)
        }
    }
}


@Composable
fun ImageScreen2(imageViewModel: LoadImageViewModel2) {
    // 获取当前图片状态————由ViewModel管理状态值的变化
    val requestState = imageViewModel.requestState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // TODO：动态显示请求结果（正在：加载中/成功：URL/失败：加载在线图片资源失败）
            Text(requestState.value.toString(), fontSize = 20.sp)
            when (requestState.value) {
                is Result2.Loading -> {
                    CircularProgressIndicator()
                }

                is Result2.Success<Any> -> {
                    AsyncImage(
                        modifier = Modifier
                            .width(400.dp)
                            .height(400.dp),
                        model = (requestState.value as Result2.Success).data,
                        contentDescription = null
                    )
                }

                is Result2.Error -> {
                    Text(
                        text = (requestState.value as Result2.Error).errorDesc.toString(),
                        fontSize = 16.sp
                    )
                }

                is Result2.NotBegin -> {
                    Text(text = "点击按钮加载图片", fontSize = 16.sp)
                }
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

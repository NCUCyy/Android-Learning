package com.cyy.app.ch06

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.request.ImageRequest
import kotlinx.coroutines.delay

sealed class OpResult<T>() {
    object Loading : OpResult<ImageBitmap>()
    object Error : OpResult<ImageBitmap>()
    data class Success(val image: ImageBitmap) : OpResult<ImageBitmap>()
}

class ImageRepository constructor(context: Context) {
    val context: Context = context
    var id = 0

    //https://wome.com网站不存在，网站的图片链接均为假，调试时请自行设置
    val imageLst = listOf(
        "https://img0.baidu.com/it/u=3368678403,249914024&fm=253&fmt=auto&app=138&f=JPEG?w=889&h=500",
        "https://img1.baidu.com/it/u=1586503404,2024787974&fm=253&app=120&size=w931&n=0&f=JPEG&fmt=auto?sec=1701882000&t=d5aac50f5ea61054720081dd478a7939",
        "https://img0.baidu.com/it/u=678433132,1708154179&fm=253&app=138&size=w931&n=0&f=JPEG&fmt=auto?sec=1701882000&t=efaa22401649b017f82e9666a6cd72bb",
    )

    fun findByUrl(url: String) = imageLst.indexOf(url)

    //请求下一个图片资源的url字符串
    suspend fun next(): String {
        id = (id + 1) % imageLst.size
        return imageLst.get(id)
    }

    //请求上一个图片资源的url字符串
    suspend fun prev(): String {
        id = (id - 1 + imageLst.size) % imageLst.size
        return imageLst.get(id)
    }

    // 加载在线图片资源，返回ImageBitmap
    suspend fun loadImageByUrl(url: String): ImageBitmap {
        //请求在线图片
        val request = ImageRequest.Builder(context).data(url).build()
        val imageLoader by lazy { ImageLoader(context) }
        return (imageLoader.execute(request).drawable as BitmapDrawable)
            .bitmap.asImageBitmap().apply {
                //延迟两秒加载图片
                delay(1000)
            }
    }
}

/**
 * 将非Composable状态转换成Composable状态
 * @param imageRepository ImageRepository 图片仓库
 * @return State<OpResult<ImageBitmap>>
 */
@Composable
fun loadNetworkImages(imageRepository: ImageRepository): State<OpResult<ImageBitmap>> {
    return produceState<OpResult<ImageBitmap>>(initialValue = OpResult.Loading) {
        for (i in 0 until imageRepository.imageLst.size) {
            val image = imageRepository.loadImageByUrl(imageRepository.imageLst[i])
            value = if (image == null) {
                OpResult.Error
            } else {
                OpResult.Success(image)
            }
        }
    }
}

@Composable
fun ImageScreen(imageRepository: ImageRepository) {
    val result by loadNetworkImages(imageRepository = imageRepository) //从图片仓库中请求资源，返回包含请求结果的状态
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (result) {
                is OpResult.Success -> {
                    Image(
                        modifier = Modifier.size(400.dp, 300.dp),
                        bitmap = (result as OpResult.Success).image,
                        contentDescription = null
                    )
                }
                is OpResult.Error -> {
                    Image(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = null,
                        modifier = Modifier.size(200.dp, 200.dp)
                    )
                }
                else -> {//等待加载图片时，显示圆形进度条
                    CircularProgressIndicator()
                }
            }
        }
    }
}

class ImageActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val imageRepository = ImageRepository(this)
        setContent {
            ImageScreen(imageRepository)
        }
    }
}

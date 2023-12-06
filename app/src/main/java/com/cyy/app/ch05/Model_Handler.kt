//package com.cyy.app.ch05
//
//import android.os.Bundle
//import android.os.Handler
//import android.os.Looper
//import android.os.Message
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.BorderStroke
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.width
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.RadioButton
//import androidx.compose.material3.RadioButtonDefaults
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextButton
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.lifecycle.ViewModel
//import coil.compose.AsyncImage
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlin.concurrent.thread
//
///**
// * 数据层：定义数据存储和要访问处理的数据；
// * ViewModel层：是视图模型层，承担【业务逻辑的定义】和【更新界面的任务】
// * -> ViewModel调用数据层的数据，经过业务处理得到新的数据，用这些数据去修改UI层的界面；
// * UI层：界面层，可以观察VIewModel内存储状态数据的变化，根据ViewModel提供的数据，更新界面。
// *
// * 整体代码思路：
// * Screen中点击按钮，触发repository中的requestImage()————开始自动播放图片的方法
// * repository中的requestImage()方法，定义了工作线程，每隔1秒，就通过Handler发送一条消息（包含：图片的url和radio的下标）
// * 在MainActivity中接收到这个Message，并修改ViewModel中的值
// * ViewModel中的值发生变化，UI层就会自动更新，即显示对应的图片以及对应下标的Radio选中
// * （一）补充实现1：点击Radio，触发ViewModel中的requestSingleImage()方法，发送一条消息，修改ViewModel中的值
// * （二）补充实现2：点击充值按钮，修改ViewModel中的imageUrl和imageId的值，UI层会自动更新
// */
//class ImageRepository(private val handler: Handler) {
//    //如果需要测试，请自行在网络中查找图片资源，下列的网址均为不存在，只为示例而已。
//    val imageLst: List<String> = listOf(
//        "https://img0.baidu.com/it/u=3368678403,249914024&fm=253&fmt=auto&app=138&f=JPEG?w=889&h=500",
//        "https://img1.baidu.com/it/u=1586503404,2024787974&fm=253&app=120&size=w931&n=0&f=JPEG&fmt=auto?sec=1701882000&t=d5aac50f5ea61054720081dd478a7939",
//        "https://img0.baidu.com/it/u=678433132,1708154179&fm=253&app=138&size=w931&n=0&f=JPEG&fmt=auto?sec=1701882000&t=efaa22401649b017f82e9666a6cd72bb",
//    )
//
//    /**
//     * 根据列表的索引号获取图像的URL(循环播放照片)
//     */
//    fun requestImage() {
//        //自定义工作线程
//        thread {
//            for (imageId in imageLst.indices) {
//                Thread.sleep(1000)
//                // 创建消息对象
//                var message = Message.obtain()
//                // 设置消息标识
//                message.what = 0x123
//                // 传递图片的URL
//                message.obj = imageLst[imageId]
//                // 传递图片的索引(用于radio显示)
//                message.arg1 = imageId
//                // 发送消息
//                handler.sendMessage(message)
//            }
//        }
//    }
//
//    /**
//     * 点击Radio请求单个照片
//     */
//    fun requestSingleImage(idx: Int) {
//        thread {
//            var message = Message.obtain()
//            message.what = 0x123
//            message.arg1 = idx
//            message.obj = imageLst[idx]
//            handler.sendMessage(message)
//        }
//    }
//}
//
//class LoadImageViewModel(private val imageRepository: ImageRepository) : ViewModel() {
//    private val _currentImageId = MutableStateFlow(0)
//    val currentImageId = _currentImageId.asStateFlow()
//
//    private val _currentImage = MutableStateFlow("")
//    val currentImage = _currentImage.asStateFlow()
//
//    /**
//     * Change image index
//     * 修改图片索引
//     */
//    fun changeImageIndex(index: Int) {
//        _currentImageId.value = index % imageRepository.imageLst.size
//    }
//
//    /**
//     * 修改当前的图片链接
//     */
//    fun changeImage(url: String) {
//        _currentImage.value = url
//    }
//
//    /**
//     * Request image
//     * 请求图片
//     */
//    fun requestImage() {
//        imageRepository.requestImage()
//    }
//
//    fun requestSingleImage(idx: Int) {
//        imageRepository.requestSingleImage(idx)
//    }
//
//    fun clearAll() {
//        _currentImage.value = ""
//        _currentImageId.value = 0
//    }
//}
//
//class MainActivity : ComponentActivity() {
//    lateinit var viewModel: LoadImageViewModel
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            //定义Handler对象
//            val handler = object : Handler(Looper.getMainLooper()) {
//                override fun handleMessage(msg: Message) {
//                    super.handleMessage(msg)
//                    if (msg.what == 0x123) {
//                        //修改图片
//                        viewModel.changeImage(msg.obj as String)
//                        //修改图片索引
//                        viewModel.changeImageIndex(msg.arg1)
//                    }
//                }
//            }
//
//            val imageRepository = ImageRepository(handler)
//            viewModel = LoadImageViewModel(imageRepository)
//
//            //加载界面
//            ImageScreen(viewModel = viewModel, repository = imageRepository)
//        }
//    }
//}
//
//@Composable
//fun ImageScreen(viewModel: LoadImageViewModel, repository: ImageRepository) {
//    //数据层中保存的在线图片列表
//    val images = repository.imageLst
//    //获取当前图片状态
//    val imageURLState = viewModel.currentImage.collectAsState()
//    //获取当前图片索引
//    val imageIdState = viewModel.currentImageId.collectAsState()
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(20.dp)
//            .background(Color.Black),
//        contentAlignment = Alignment.Center
//    ) {
//        Column(
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
//            if (imageURLState.value.isNotBlank()) {
//                AsyncImage(
//                    modifier = Modifier
//                        .width(400.dp)
//                        .height(400.dp)
//                        .border(BorderStroke(1.dp, Color.Blue)),
//                    model = imageURLState.value,
//                    contentDescription = null
//                )
//            } else
//                Text(
//                    modifier = Modifier
//                        .width(300.dp)
//                        .height(500.dp),
//                    text = "等待加载图片", fontSize = 20.sp, textAlign = TextAlign.Center, color = Color.White
//                )
//            if (imageURLState.value.isNotBlank())
//                Row(horizontalArrangement = Arrangement.Center) {
//                    for (i in images.indices) {
//                        RadioButton(
//                            colors = RadioButtonDefaults.colors(selectedColor = Color.Green),
//                            selected = i == imageIdState.value,
//                            onClick = {
//                                viewModel.requestSingleImage(i)
//                            }
//                        )
//                    }
//                }
//
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(end = 10.dp), horizontalArrangement = Arrangement.Center
//            ) {
//
//                TextButton(colors = ButtonDefaults.buttonColors(
//                    contentColor = Color.Blue,
//                    containerColor = Color.LightGray
//                ),
//                    onClick = {
//                        //请求图片
//                        viewModel.requestImage()
//                    }) {
//                    Text("动态显示图片", fontSize = 16.sp)
//                }
//
//                TextButton(colors = ButtonDefaults.buttonColors(
//                    contentColor = Color.Blue,
//                    containerColor = Color.LightGray
//                ), onClick = {
//                    // 只需要：清除ViewModel中的imageUrl、imageId的值即可（页面会跟着变化）
//                    viewModel.clearAll()
//                }) {
//                    Text("重置", fontSize = 16.sp)
//                }
//            }
//        }
//    }
//}

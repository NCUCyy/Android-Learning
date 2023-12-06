package com.cyy.app.ch06

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

class LaunchedEffectTest : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen2()
        }
    }
}

//全局变量，在MainScreen作用域外
val timer2 = mutableIntStateOf(0)

// -------------------------------------LaunchedEffect-------------------------------------
/**
 * LaunchedEffect函数可以在某个可组合项的作用域内运行挂起函数时，它会启动内部的代码块到协程上下文CoroutineContext中。
 * 当函数的key1的值发生变化，会重构LaunchedEffect。这时，LaunchedEffect原来启动的协程会被取消然后又重新启动。
 * 当LaunchedEffect退出组合项时，协程会被取消。LaunchedEffect可组合函数定义如下：
 * 1. key1：表示关键字可以是任何类型，如果是可变的状态值时，可以根据可变状态值的变化，取消原有协程并启动新的协程。
 *      - 当key1为Unit或true时，LaunchedEffect函数将与当前重组函数保持一致的生命周期。
 * 2. block:表示要调用的挂起函数。需要在协程范围中运行；
 *
 * 注意点：LaunchedEffect函数中的键参数为Unit/True时，这表示在MainScreen函数被调用时或重新组合时，才会加载LaunchedEffect函数。
 * 并不存在【键参数值的变化】重新加载协程代码的可能。
 */
@Composable
fun MainScreen2() {
    Log.d("TAG------------->", "MainScreen2")
    //函数作用域内的变量
    var runningState = remember { mutableStateOf(false) }

    /**
     * 两种方案：
     * 1. 不需要runningState，只需要把timer2的值作为key1即可，然后在协程中对其+1（每隔1秒），然后被LaunchedEffect函数捕获到key1值的变化，又会重构LaunchedEffect函数
     * 即取消原协程，创建一个新的协程————这样虽然可行，但是要频繁的取消和创建协程，代价高昂
     * 2. 额外定义一个runningState，当点击计时按钮时，runningState的值为true，这样LaunchedEffect函数就会执行，然后在协程中对timer2的值+1（每隔1秒）
     * 即只需要在runningState变化的时候，才需要取消和创建协程————这样就可以避免频繁的取消和创建协程（推荐）
     * - 但是需要再协程内部定义一下，runningState虽然变化了，但是只有当他是True的时候才能开始计时，否则不计时————>用while(runningState.value)来保证
     */
    // TODO：初始刚进入组合函数的时候也会执行一次！
    LaunchedEffect(key1 = runningState.value) {
        // TODO：每次当key的值发生改变时，就会取消原来的协程，创建一个新的协程
        // TODO：所以还需要一个while来保证只有在runningState.value为true的时候才会执行计时任务
        while (runningState.value) {
            delay(1000)
            timer2.intValue += 1
            Log.d("TAG", "${timer2.value} seconds")
        }

    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "计时器", fontSize = 30.sp, color = MaterialTheme.colorScheme.primary)
            Text(text = "${timer2.value}秒", fontSize = 24.sp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(modifier = Modifier.width(100.dp),
                    onClick = {
                        runningState.value = true
                    }) {
                    Row {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            tint = Color.Green,
                            contentDescription = null
                        )
                        Text("计时")
                    }
                }
                IconButton(modifier = Modifier.width(100.dp),
                    onClick = {
                        runningState.value = false
                    }) {
                    Row {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            tint = Color.Green, contentDescription = null
                        )
                        Text("停止")
                    }
                }
            }
        }
    }
}

// -------------------------------------rememberUpdatedState-------------------------------------
/**
 * 1、LaunchedEffect函数中的键参数为Unit，这表示在MainScreen函数被调用时或重新组合时，才会加载LaunchedEffect函数。并不存在键参数值的变化重新加载协程代码的可能。
 * 2、但是，通过rememberUpdatedState(newValue = timer)函数，一致可以通过timerState.value来获取变化的状态timer.
 * 3、在上述代码中，由于runningState.value初始值为true,因此一启动MainScreen，就会显示显示动态计时的效果。
 * 但是，当点击停止按钮runningState.value的值设置为false，导致代码段停止运行。然后再点击"计时“按钮，可以发现，LaunchedEffect函数并没有重启。
 */
@Composable
fun MainScreen3() {
    //函数作用域内
    var runningState = remember { mutableStateOf(true) }
    val timerState = rememberUpdatedState(newValue = timer2)

    /**
     * 每隔一秒组合函数都会重启一次
     */
    LaunchedEffect(Unit) {

        while (runningState.value) {
            delay(1000)
            timerState.value.value += 1
            Log.d("TAG", "${timer2.value} m")
        }

    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "计时器", fontSize = 30.sp, color = MaterialTheme.colorScheme.primary)
            Text(text = "${timerState.value.value}秒", fontSize = 24.sp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(modifier = Modifier.width(100.dp),
                    onClick = {
                        runningState.value = true
                    }) {
                    Row {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            tint = Color.Green,
                            contentDescription = null
                        )
                        Text("计时")
                    }
                }
                IconButton(modifier = Modifier.width(100.dp),
                    onClick = {
                        runningState.value = false
                    }) {
                    Row {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            tint = Color.Green, contentDescription = null
                        )
                        Text("停止")
                    }
                }
            }
        }
    }
}

package com.cyy.exp2.test_handler


import android.app.Application
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class MyApplication : Application() {
    //获取可用核心的数量,可供 Java 虚拟机使用的处理器数
    private val NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors()

    //创建线程体队列
    private val workQueue: BlockingQueue<Runnable> = LinkedBlockingQueue<Runnable>()

    //设置闲时线程的在终端线程前的等待时间wqazzb
    private val KEEP_ALIVE_TIME = 1L

    //设置时间单位为秒
    private val KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS

    //创建一个线程池管理器
    val threadPoolExecutor: ThreadPoolExecutor = ThreadPoolExecutor(
        NUMBER_OF_CORES,   //初始化线程池的大小
        NUMBER_OF_CORES,   //最大线程池的个数
        KEEP_ALIVE_TIME,
        KEEP_ALIVE_TIME_UNIT,
        workQueue
    )
}

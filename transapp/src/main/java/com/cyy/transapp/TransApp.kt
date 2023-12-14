package com.cyy.transapp

import android.app.Application
import com.cyy.transapp.network.SerializationConverter
import com.cyy.transapp.repository.QueryRepository
import com.cyy.transapp.repository.TransRepository
import com.drake.net.BuildConfig
import com.drake.net.NetConfig
import com.drake.net.okhttp.setConverter
import com.drake.net.okhttp.setDebug
import java.util.concurrent.TimeUnit

class TransApp : Application() {
    // 通过 lazy，数据库和存储库只在需要时创建，而不是在应用程序启动时创建
    private val database by lazy { AppDataBase.getDatabase(this) }


    val transRepository by lazy { TransRepository(database.getTransRecordDao()) }
    val queryRepository by lazy { QueryRepository() }
    override fun onCreate() {
        NetConfig.initialize("", this) {
            // 超时配置, 默认是10秒, 设置太长时间会导致用户等待过久
            connectTimeout(30, TimeUnit.SECONDS)
            readTimeout(30, TimeUnit.SECONDS)
            writeTimeout(30, TimeUnit.SECONDS)
            setDebug(BuildConfig.DEBUG)
            setConverter(SerializationConverter())
        }
        super.onCreate()
    }
}
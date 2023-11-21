package com.cyy.app.room

import android.app.Application

// 需要在MainFest中配置
class MyApp : Application() {
    // 通过 lazy，数据库和存储库只在需要时创建，而不是在应用程序启动时创建
    private val database by lazy { AppDataBase.getDatabase(this) }

    val userRepository by lazy { UserRepository(database.getUserDao()) }
    val recordRepository by lazy { RecordRepository(database.getRecordDao()) }

    override fun onCreate() {
        super.onCreate()
    }
}
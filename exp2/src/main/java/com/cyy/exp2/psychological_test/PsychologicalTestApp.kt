package com.cyy.exp2.psychological_test

import android.app.Application
import androidx.activity.ComponentActivity
import com.cyy.exp2.psychological_test.repository.RecordRepository
import com.cyy.exp2.psychological_test.repository.UserRepository

class PsychologicalTestApp : Application() {
    // 通过 lazy，数据库和存储库只在需要时创建，而不是在应用程序启动时创建
    private val database by lazy { AppDataBase.getDatabase(this) }

    val userRepository by lazy { UserRepository(database.getUserDao()) }

    val recordRepository by lazy { RecordRepository(database.getRecordDao()) }
    override fun onCreate() {
        super.onCreate()
    }
}
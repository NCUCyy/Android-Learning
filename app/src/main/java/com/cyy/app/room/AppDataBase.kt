package com.cyy.app.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// 定义一个数据库类，用注解 @Database 标记，并将实体类的数组作为参数传递（会自动创建对应的数据表）
@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class AppDataBase : RoomDatabase() {

    abstract fun getUserDao(): UserDao

    // 创建一个单例对象，避免同时打开多个数据库实例
    companion object {
        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getDatabase(context: Context): AppDataBase {
            return INSTANCE ?: synchronized(this) {
                val dataBase = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java, "room_test."
                ).build()
                INSTANCE = dataBase
                dataBase
            }
        }
    }
}

package com.cyy.transapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.cyy.transapp.dao.PlanDao
import com.cyy.transapp.dao.StarWordDao
import com.cyy.transapp.dao.TodayDao
import com.cyy.transapp.dao.TransRecordDao
import com.cyy.transapp.dao.UserDao
import com.cyy.transapp.pojo.Converters
import com.cyy.transapp.pojo.Plan
import com.cyy.transapp.pojo.StarWord
import com.cyy.transapp.pojo.Today
import com.cyy.transapp.pojo.TransRecord
import com.cyy.transapp.pojo.User

/**
 * Room 是 SQLite 数据库之上的一个数据库层。它负责处理平常使用 SQLiteOpenHelper 所处理的单调乏味的任务。
 * 通常，整个应用只需要一个 Room 数据库实例。Room 实例管理多个 Dao 对象，具体查询请求是通过 Dao 对象完成的。
 *
 * 为避免界面性能不佳，默认情况下，Room 不允许在主线程上发出查询请求。当 Room 查询返回 Flow 时，这些查询会在后台线程上自动异步运行。
 *
 * 1、Room 数据库类必须是 abstract 且继承 RoomDatabase 类
 * 2、通过 @Database 将该类注解为 Room 数据库，并使用注解参数声明数据库中的实体以及设置版本号。每个实体都对应一个将在数据库中创建的表。
 * 3、数据库会通过每个 @Dao 的抽象“getter”方法公开 DAO。虽然您不能提供一个具体的类，但是 Room 会为您的每个 DAO 类生成一个具体的类。
 * 4、定义一个单例 AppDataBase,，以防出现同时打开数据库的多个实例的情况。
 * 5、getDatabase 会返回该单例。首次使用时，它会创建数据库，具体方法是：使用 Room 的数据库构建器在 AppDataBase 类的应用上下文中创建 RoomDatabase 对象，并指定数据库的名称为 “app_database”。
 */
// 定义一个数据库类，用注解 @Database 标记，并将实体类的数组作为参数传递（会自动创建对应的数据表）
@Database(
    entities = [TransRecord::class, User::class, StarWord::class, Today::class, Plan::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDataBase : RoomDatabase() {

    abstract fun getTransRecordDao(): TransRecordDao
    abstract fun getUserDao(): UserDao
    abstract fun getStarWordDao(): StarWordDao
    abstract fun getTodayDao(): TodayDao
    abstract fun getPlanDao(): PlanDao

    // 创建一个单例对象，避免同时打开多个数据库实例
    companion object {
        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getDatabase(context: Context): AppDataBase {
            return INSTANCE ?: synchronized(this) {
                val dataBase = Room.databaseBuilder(
                    context.applicationContext,
                    // 若修改了pojo，则需要修改这里的数据库名称（即：创建一个新的SQLite数据库）
                    AppDataBase::class.java, "db10"
                )
                    .build()
                INSTANCE = dataBase
                dataBase
            }
        }
    }
}

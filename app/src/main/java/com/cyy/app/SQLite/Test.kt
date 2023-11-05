package com.cyy.app.SQLite

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "sqlite_test"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        // 在此方法中创建数据库表格
        db.execSQL("CREATE TABLE IF NOT EXISTS test (id INTEGER PRIMARY KEY, name TEXT, age INTEGER)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // 在数据库版本升级时执行必要的操作
        db.execSQL("DROP TABLE IF EXISTS test")
        onCreate(db)
    }
}


class DatabaseManager(context: Context) {

    private val dbHelper: DatabaseHelper = DatabaseHelper(context)
    private var database: SQLiteDatabase? = null

    fun open() {
        try {
            database = dbHelper.writableDatabase
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun close() {
        dbHelper.close()
    }

    fun insertData(name: String, age: Int) {
        val values = ContentValues()
        values.put("name", name)
        values.put("age", age)
        database?.insert("test", null, values)
    }

    fun getAllData(): Cursor? {
        return database?.query("test", null, null, null, null, null, null)
    }
}

class ActivityTest : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }
}

@SuppressLint("Range")
@Composable
@Preview
fun MainScreen() {
    val context = LocalContentColor.current as Activity
    val databaseManager = DatabaseManager(context)
    databaseManager.open()

    // 插入数据
    databaseManager.insertData("John", 30)

    // 获取数据
    val cursor = databaseManager.getAllData()
    if (cursor != null) {
        if (cursor.moveToFirst()) {
            do {
                val name = cursor.getString(cursor.getColumnIndex("name"))
                val age = cursor.getInt(cursor.getColumnIndex("age"))
                // 处理数据
            } while (cursor.moveToNext())
        }
        cursor.close()
    }

    databaseManager.close()

}

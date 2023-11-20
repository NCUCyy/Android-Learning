package com.cyy.app.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert
    suspend fun insert(vararg user: User) // 注意是挂起函数

    @Update
    suspend fun update(vararg user: User)

    @Delete
    suspend fun delete(vararg user: User)

    @Query("DELETE FROM t_user") // 表名会自动转大写
    suspend fun deleteAll()

    @Query(value = "SELECT * FROM t_user")
    fun getAllUser(): Flow<List<User>>
}

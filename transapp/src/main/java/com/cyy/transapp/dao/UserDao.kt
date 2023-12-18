package com.cyy.transapp.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.cyy.transapp.pojo.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert
    suspend fun insert(vararg user: User) // 注意是挂起函数

    @Update
    suspend fun update(vararg user: User)

    @Delete
    suspend fun delete(vararg user: User)

    @Query("DELETE FROM users") // 表名会自动转大写
    suspend fun deleteAll()

    /**
     * 【观察数据库的变化】
     * 当数据发生变化时，您通常需要执行某些操作，例如在界面中显示更新后的数据。这意味着您必须观察数据，以便在数据发生变化后作出回应。
     *
     * 为了观察数据变化情况，推荐使用 kotlin 协程中的 Flow。
     * 只需将查询方法的返回值类型改成使用 Flow 类型；当数据库更新时，Room 会自动生成更新 Flow 所需的所有代码。
     */
    @Query(value = "SELECT * FROM users")
    fun getAllUser(): Flow<List<User>>

    @Query("SELECT * from users WHERE username = :username")
    suspend fun getByUsername(username: String): User

    @Query("SELECT * from users WHERE id = :id")
    fun getFlowById(id: Int): Flow<User>

    @Query("SELECT * from users WHERE id = :id")
    suspend fun getById(id: Int): User

    @Query("SELECT * from users WHERE username = :username and password = :password")
    suspend fun getByUsernameAndPassword(username: String, password: String): User
}
package com.cyy.transapp.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.cyy.transapp.pojo.StarWord
import kotlinx.coroutines.flow.Flow

@Dao
interface StarWordDao {

    @Insert
    suspend fun insert(vararg starWord: StarWord) // 注意是挂起函数

    @Update
    suspend fun update(vararg starWord: StarWord)

    @Delete
    suspend fun delete(vararg starWord: StarWord)

    @Query("DELETE FROM star_words WHERE userId = :userId AND word = :word")
    suspend fun deleteByUserIdAndWord(userId: Int, word: String)

    @Query("DELETE FROM star_words") // 表名会自动转大写
    suspend fun deleteAll()

    /**
     * 【观察数据库的变化】
     * 当数据发生变化时，您通常需要执行某些操作，例如在界面中显示更新后的数据。这意味着您必须观察数据，以便在数据发生变化后作出回应。
     *
     * 为了观察数据变化情况，推荐使用 kotlin 协程中的 Flow。
     * 只需将查询方法的返回值类型改成使用 Flow 类型；当数据库更新时，Room 会自动生成更新 Flow 所需的所有代码。
     */
    @Query("SELECT * from star_words WHERE userId = :userId ORDER BY addTime")
    fun getAllByUserId(userId: Int): Flow<List<StarWord>>

    @Query("SELECT * from star_words WHERE userId = :userId and word = :word")
    suspend fun getStarWordByUserIdAndWord(userId: Int, word: String): StarWord
}
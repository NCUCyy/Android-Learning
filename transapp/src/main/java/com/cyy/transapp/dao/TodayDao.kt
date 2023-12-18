package com.cyy.transapp.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.cyy.transapp.pojo.Today
import kotlinx.coroutines.flow.Flow

@Dao
interface TodayDao {

    @Insert
    suspend fun insert(vararg today: Today) // 注意是挂起函数

    @Update
    suspend fun update(vararg today: Today)

    @Query("SELECT * FROM todays WHERE userId = :userId AND year = :year AND month = :month AND day = :day")
    suspend fun getByUserIdAndYMD(userId: Int, year: Int, month: Int, day: Int): Today

    @Query("SELECT * FROM todays WHERE userId = :userId AND year = :year AND month = :month AND day = :day")
    fun getFlowByUserIdAndYMD(userId: Int, year: Int, month: Int, day: Int): Flow<Today>
}
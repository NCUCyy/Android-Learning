package com.cyy.transapp.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.cyy.transapp.pojo.Plan
import kotlinx.coroutines.flow.Flow

@Dao
interface PlanDao {
    @Insert
    suspend fun insert(vararg plan: Plan)

    @Update
    suspend fun update(vararg plan: Plan)

    @Query("SELECT * FROM plans WHERE userId = :userId AND vocabulary = :vocabulary")
    suspend fun getByUserIdAndVocabulary(userId: Int, vocabulary: String): Plan

    @Query("SELECT * FROM plans WHERE userId = :userId AND vocabulary = :vocabulary")
    fun getFlowByUserIdAndVocabulary(userId: Int, vocabulary: String): Flow<Plan>
}
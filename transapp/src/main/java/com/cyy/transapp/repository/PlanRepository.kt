package com.cyy.transapp.repository

import androidx.annotation.WorkerThread
import com.cyy.transapp.dao.PlanDao
import com.cyy.transapp.pojo.Plan
import kotlinx.coroutines.flow.Flow

class PlanRepository(private val planDao: PlanDao) {
    @WorkerThread
    suspend fun insert(vararg plan: Plan) {
        planDao.insert(*plan)
    }

    @WorkerThread
    suspend fun update(vararg plan: Plan) {
        planDao.update(*plan)
    }

    @WorkerThread
    suspend fun getByUserIdAndVocabulary(userId: Int, vocabulary: String): Plan {
        return planDao.getByUserIdAndVocabulary(userId, vocabulary)
    }

    fun getFlowByUserIdAndVocabulary(userId: Int, vocabulary: String): Flow<Plan> {
        return planDao.getFlowByUserIdAndVocabulary(userId, vocabulary)
    }
}
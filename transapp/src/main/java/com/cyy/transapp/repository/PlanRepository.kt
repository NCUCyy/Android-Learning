package com.cyy.transapp.repository

import androidx.annotation.WorkerThread
import com.cyy.transapp.dao.PlanDao
import com.cyy.transapp.pojo.Plan

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
}
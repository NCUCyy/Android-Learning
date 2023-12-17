package com.cyy.transapp.repository

import androidx.annotation.WorkerThread
import com.cyy.transapp.dao.TodayDao
import com.cyy.transapp.pojo.Today


class TodayRepository(private val todayDao: TodayDao) {

    @WorkerThread
    suspend fun insert(vararg today: Today) {
        todayDao.insert(*today)
    }

    @WorkerThread
    suspend fun update(vararg today: Today) {
        todayDao.update(*today)
    }

    @WorkerThread
    suspend fun getByUserIdAndYMD(userId: Int, year: Int, month: Int, day: Int): Today {
        return todayDao.getByUserIdAndYMD(userId, year, month, day)
    }
}
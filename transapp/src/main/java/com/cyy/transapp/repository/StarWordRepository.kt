package com.cyy.transapp.repository

import androidx.annotation.WorkerThread
import com.cyy.transapp.dao.StarWordDao
import com.cyy.transapp.pojo.StarWord
import kotlinx.coroutines.flow.Flow

class StarWordRepository(private val starWordDao: StarWordDao) {
    @WorkerThread
    suspend fun insert(vararg starWord: StarWord) {
        starWordDao.insert(*starWord)
    }

    @WorkerThread
    suspend fun update(vararg starWord: StarWord) {
        starWordDao.update(*starWord)
    }

    @WorkerThread
    suspend fun delete(vararg starWord: StarWord) {
        starWordDao.delete(*starWord)
    }

    @WorkerThread
    suspend fun deleteByUserIdAndWord(userId: Int, word: String) {
        starWordDao.deleteByUserIdAndWord(userId, word)
    }

    @WorkerThread
    suspend fun deleteAll() {
        starWordDao.deleteAll()
    }

    @WorkerThread
    fun getAllByUserId(userId: Int): Flow<List<StarWord>> {
        return starWordDao.getAllByUserId(userId)
    }

    @WorkerThread
    suspend fun getStarWordByUserIdAndWord(userId: Int, word: String): StarWord {
        return starWordDao.getStarWordByUserIdAndWord(userId, word)
    }

    @WorkerThread
    fun getFlowStarWordByUserIdAndWord(userId: Int, word: String): Flow<StarWord> {
        return starWordDao.getFlowStarWordByUserIdAndWord(userId, word)
    }
}
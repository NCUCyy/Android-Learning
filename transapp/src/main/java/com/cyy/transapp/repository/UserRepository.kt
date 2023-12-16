package com.cyy.transapp.repository

import androidx.annotation.WorkerThread
import com.cyy.transapp.dao.UserDao
import com.cyy.transapp.pojo.User
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {
    // 默认情况下，Room 会在非主线程执行挂起函数进行查询，
    // 因此，我们不需要实现其他任何东西来确保避免在主线程中执行过长时间的数据操作。
    @WorkerThread
    suspend fun insert(vararg user: User) {
        userDao.insert(*user)
    }

    @WorkerThread
    suspend fun update(vararg user: User) {
        userDao.update(*user)
    }

    @WorkerThread
    suspend fun delete(vararg user: User) {
        userDao.delete(*user)
    }

    @WorkerThread
    suspend fun deleteAll() {
        userDao.deleteAll()
    }

    @WorkerThread
    suspend fun getByUsername(username: String): User {
        return userDao.getByUsername(username)
    }

    @WorkerThread
    fun getById(id: Int): Flow<User> {
        return userDao.getById(id)
    }

    @WorkerThread
    suspend fun getByUsernameAndPassword(username: String, password: String): User {
        return userDao.getByUsernameAndPassword(username, password)
    }
}

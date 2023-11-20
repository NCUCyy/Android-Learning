package com.cyy.app.room

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

// 将DAO声明为构造函数中的私有属性。传入 DAO 而不是整个数据库对象，因为你只需要访问DAO。
class UserRepository(private val userDao: UserDao) {

    // Room 在单独的线程上执行所有查询。当数据发生变化时，作为被观察对象的 Flow 会通知观察者。
    val allUser: Flow<List<User>> = userDao.getAllUser()

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
}

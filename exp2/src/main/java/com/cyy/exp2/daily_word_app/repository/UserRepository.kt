package com.cyy.exp2.daily_word_app.repository

import androidx.annotation.WorkerThread
import com.cyy.exp2.daily_word_app.dao.UserDao
import com.cyy.exp2.daily_word_app.pojo.User
import kotlinx.coroutines.flow.Flow

/**
 * 1、Repository只需持有 DAO 对象，而非整个数据库实例对象。因为 DAO 包含了数据库的所有读取/写入方法，因此它只需要访问 DAO。
 * 2、对于 allUser 返回的是Flow对象，这是因为 userDao.getAllRecords() 返回的就是一个Flow。Room 将在单独的线程上执行所有查询。
 * 3、suspend 修饰的操作方法意味着需要从协程或其他挂起函数进行调用。默认情况下，Room 会在非主线程执行挂起函数进行查询，
 * 4、Room 在主线程之外执行挂起查询。
 * 5、Repository的用途是在不同的数据源之间进行协调。在这个简单示例中，数据源只有一个，因此该数据仓库并未执行多少操作。
 */
// 将DAO声明为构造函数中的私有属性。传入 DAO 而不是整个数据库对象，因为你只需要访问DAO。
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
}

package com.cyy.exp2.psychological_test.pojo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "t_user")
data class User(
    val username: String,// 唯一
    val password: String,
    val sex: String,
    var testTurns: Int = 0,// 测试次数
) {
    @PrimaryKey(autoGenerate = true)
    // 自增的主键，在类内部定义，不要出现在构造函数的参数中，这样在实例化时才不需要设置该值
    var id: Int = 0
}
package com.cyy.exp2.daily_word_app.pojo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "t_user")
data class User(
    var username: String,// 唯一
    var password: String,
    val sex: String,
    var testTurns: Int = 0,// 测试次数
) {
    @PrimaryKey(autoGenerate = true)
    // 自增的主键，在类内部定义，不要出现在构造函数的参数中，这样在实例化时才不需要设置该值
    var id: Int = 0
}
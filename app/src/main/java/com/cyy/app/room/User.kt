package com.cyy.app.room

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "t_user")
data class User(
    @PrimaryKey val id: Int,
    val username: String,
    val password: String,
    val sex: String,
    @Ignore val records: MutableList<Int>
) {
    // 对于有@Ignore注解的属性，需要提供一个构造函数（不包含它）
    constructor(
        id: Int,
        username: String,
        password: String,
        sex: String
    ) : this(id, username, password, sex, mutableListOf())
}

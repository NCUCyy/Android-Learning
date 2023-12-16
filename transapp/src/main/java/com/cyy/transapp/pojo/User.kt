package com.cyy.transapp.pojo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
class User {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var username: String = ""
    var password: String = ""
    var nickname: String = ""
    var profile: String = ""
    var iconId: Int = 0
    var todayId: Int = 0
    var planId: Int = 0
    var starBookId: Int = 0

    // 空参构造器
    constructor() {

    }

    // 用于注册---简要信息（其他默认）
    constructor(username: String, password: String) : this() {
        this.username = username
        this.password = password
    }
}

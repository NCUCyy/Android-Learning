package com.cyy.transapp.pojo

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.cyy.transapp.R

@Entity(tableName = "users")
class User {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var username: String = ""
    var password: String = ""
    var nickname: String = ""
    var profile: String = ""
    var iconId: Int = R.drawable.user
    var vocabulary: String = "未选择"


    constructor()

    @Ignore
    // 用于注册---简要信息（其他默认）
    constructor(username: String, password: String) : this() {
        this.username = username
        this.password = password
    }
}

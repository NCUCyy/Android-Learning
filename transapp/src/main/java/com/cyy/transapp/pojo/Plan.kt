package com.cyy.transapp.pojo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plans")
class Plan {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0 // planId

    // 用于唯一定位
    var userId: Int = 0
    var vocabulary: String = ""

    // 学习进度
    var learnProcess: String = ""

    // 复习进度
    var reviewProcess: String = ""

    // 每日一组学习数量
    var dailyNum = 0

    constructor() {

    }

    constructor(userId: Int, vocabulary: String) {
        this.userId = userId
        this.vocabulary = vocabulary
    }
}
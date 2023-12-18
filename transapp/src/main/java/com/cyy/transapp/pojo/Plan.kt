package com.cyy.transapp.pojo

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.cyy.transapp.model.LearnProcess
import com.cyy.transapp.model.ReviewProcess
import com.google.gson.Gson

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
    var dailyNum = 10 // 默认

    constructor() {
    }

    constructor(userId: Int) {
        this.userId = userId
    }

    @Ignore
    constructor(userId: Int, vocabulary: String) {
        this.userId = userId
        this.vocabulary = vocabulary
        // TODO：初始化！
        this.learnProcess = Gson().toJson(LearnProcess())
        this.reviewProcess = Gson().toJson(ReviewProcess())
    }
}
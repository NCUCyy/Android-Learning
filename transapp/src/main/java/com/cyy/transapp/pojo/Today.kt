package com.cyy.transapp.pojo

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.OffsetDateTime

@Entity(tableName = "todays")
class Today {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    // 用于唯一定位
    var userId: Int = 0
    var year: Int = OffsetDateTime.now().year
    var month: Int = OffsetDateTime.now().monthValue
    var day: Int = OffsetDateTime.now().dayOfMonth

    // 用于UI显示
    var newLearnNum: Int = 0 // 今日新学单词个数
    var reviewNum: Int = 0 // 今日复习单词个数
    var starNum: Int = 0 // 今日收藏单词个数
    var removeNum: Int = 0 // 今日移除单词个数
    var learnTime: Int = 0 // 今日学习时长
    var openNum: Int = 1 // 今日打开APP次数

    constructor()
    constructor(userId: Int) {
        this.userId = userId
    }
}
package com.cyy.transapp.pojo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todays")
class Today {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    // 用于唯一定位
    var userId: Int = 0
    var year: Int = 0
    var month: Int = 0
    var day: Int = 0

    // 用于UI显示
    var starNum: Int = 0 // 今日收藏单词个数
    var transNum: Int = 0 // 今日翻译单词
    var learnTime: Int = 0 // 今日学习时长
    var reviewTime: Int = 0 // 今日复习时长
    var newLearnNum: Int = 0 // 今日新学单词个数
    var reviewNum: Int = 0 // 今日复习单词个数

    constructor()
    constructor(userId: Int, year: Int, month: Int, day: Int) {
        this.userId = userId
        this.year = year
        this.month = month
        this.day = day
    }
}
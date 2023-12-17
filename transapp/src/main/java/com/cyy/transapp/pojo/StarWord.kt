package com.cyy.transapp.pojo

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.time.OffsetDateTime

@Entity(tableName = "star_words")
class StarWord {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var userId: Int = 0
    var word: String = ""
    var addTime: OffsetDateTime = OffsetDateTime.now() // 用于排序

    constructor() {

    }

    @Ignore
    constructor(userId: Int, word: String) {
        this.userId = userId
        this.word = word
    }
}
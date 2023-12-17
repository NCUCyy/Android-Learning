package com.cyy.transapp.pojo

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter


@Entity(tableName = "trans_records")
class TransRecord {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var userId: Int = 0 // 用户的id
    var word: String = ""// 唯一
    var trans: String = ""// 翻译简要
    var freq: Int = 1// 查询次数（默认是1，即第一次查询）
    var lastQueryTime: OffsetDateTime = OffsetDateTime.now() // 上次查询的时间

    constructor() {
    }

    @Ignore
    constructor(
        userId: Int,
        word: String,
        trans: String,
    ) {
        this.userId = userId
        this.word = word
        this.trans = trans
    }
}

object Converters {
    private val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    @TypeConverter
    @JvmStatic
    fun toOffsetDateTime(value: String?): OffsetDateTime? {
        return value?.let {
            return formatter.parse(value, OffsetDateTime::from)
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromOffsetDateTime(date: OffsetDateTime?): String? {
        return date?.format(formatter)
    }
}

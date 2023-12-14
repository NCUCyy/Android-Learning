package com.cyy.transapp.pojo

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Entity(tableName = "trans_records")
data class TransRecord(
    var word: String,// 唯一
    var trans: String,// 翻译简要
    var freq: Int,// 查询次数
    var lastQueryTime: OffsetDateTime // 上次查询的时间
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
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

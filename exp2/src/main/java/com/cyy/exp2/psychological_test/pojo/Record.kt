package com.cyy.exp2.psychological_test.pojo

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Parcelize
@Entity(tableName = "t_record")
data class Record(
    val testTme: OffsetDateTime,
    val score: Int,
    var userId: Int = -1,// 用于查询
) : Parcelable {
    @PrimaryKey(autoGenerate = true)
    // 自增的主键，在类内部定义，不要出现在构造函数的参数中，这样在实例化时才不需要设置该值
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

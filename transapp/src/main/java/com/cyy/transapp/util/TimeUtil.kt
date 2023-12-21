package com.cyy.transapp.util

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

object TimeUtil {
    fun formatTime(time: OffsetDateTime): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日  HH:mm:ss")
        return time.format(formatter)
    }
}
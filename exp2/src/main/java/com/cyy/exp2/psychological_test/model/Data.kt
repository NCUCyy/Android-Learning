package com.cyy.exp2.psychological_test.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Data(
    @SerialName("day")
    var day: String = "",
    @SerialName("en")
    var en: String = "",
    @SerialName("month")
    var month: String = "",
    @SerialName("pic")
    var pic: String = "",
    @SerialName("zh")
    var zh: String = ""
)
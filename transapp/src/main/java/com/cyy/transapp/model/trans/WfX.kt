package com.cyy.transapp.model.trans


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WfX(
    @SerialName("name")
    var name: String = "",
    @SerialName("value")
    var value: String = ""
)
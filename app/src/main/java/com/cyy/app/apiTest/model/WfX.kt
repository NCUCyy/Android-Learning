package com.cyy.app.apiTest.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WfX(
    @SerialName("name")
    var name: String = "",
    @SerialName("value")
    var value: String = ""
)
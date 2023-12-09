package com.cyy.app.apiTest.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Web(
    @SerialName("key")
    var key: String = "",
    @SerialName("value")
    var value: List<String> = listOf()
)
package com.cyy.app.apiTest.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Dict(
    @SerialName("url")
    var url: String = ""
)
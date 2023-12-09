package com.cyy.app.apiTest.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MTerminalDict(
    @SerialName("url")
    var url: String = ""
)
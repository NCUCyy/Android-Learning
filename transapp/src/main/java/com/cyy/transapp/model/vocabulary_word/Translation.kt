package com.cyy.app.word_bank.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Translation(
    @SerialName("translation")
    var translation: String = "",
    @SerialName("type")
    var type: String = ""
)
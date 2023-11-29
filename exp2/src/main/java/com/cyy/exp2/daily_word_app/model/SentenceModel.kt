package com.cyy.exp2.daily_word_app.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SentenceModel(
    @SerialName("data")
    var `data`: Data = Data(),
    @SerialName("success")
    var success: Boolean = false
)
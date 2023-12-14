package com.cyy.transapp.model.daily_sentence


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SentenceModel(
    @SerialName("data")
    var `data`: Data = Data(),
    @SerialName("success")
    var success: Boolean = false
)
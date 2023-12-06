package com.cyy.exp2.test_handler


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SentenceModel(
    @SerialName("data")
    var `data`: Data = Data(),
    @SerialName("success")
    var success: Boolean = false
)
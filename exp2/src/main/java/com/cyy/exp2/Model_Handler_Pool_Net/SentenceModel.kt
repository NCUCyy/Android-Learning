package com.cyy.exp2.Model_Handler_Pool_Net


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SentenceModel(
    @SerialName("data")
    var `data`: Data = Data(),
    @SerialName("success")
    var success: Boolean = false
)
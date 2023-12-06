package com.cyy.exp2.Model_Handler_Image


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SentenceModel(
    @SerialName("data")
    var `data`: Data = Data(),
    @SerialName("success")
    var success: Boolean = false
)
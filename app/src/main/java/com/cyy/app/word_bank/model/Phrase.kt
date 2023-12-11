package com.cyy.app.word_bank.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Phrase(
    @SerialName("phrase")
    var phrase: String = "",
    @SerialName("translation")
    var translation: String = ""
)
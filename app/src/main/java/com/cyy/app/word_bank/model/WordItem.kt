package com.cyy.app.word_bank.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WordItem(
    @SerialName("phrases")
    var phrases: List<Phrase> = listOf(),
    @SerialName("translations")
    var translations: List<Translation> = listOf(),
    @SerialName("word")
    var word: String = ""
)
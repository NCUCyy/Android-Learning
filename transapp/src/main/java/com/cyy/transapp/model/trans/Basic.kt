package com.cyy.transapp.model.trans


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Basic(
    @SerialName("exam_type")
    var examType: List<String> = listOf(),
    @SerialName("explains")
    var explains: List<String> = listOf(),
    @SerialName("phonetic")
    var phonetic: String = "",
    @SerialName("uk-phonetic")
    var ukPhonetic: String = "",
    @SerialName("uk-speech")
    var ukSpeech: String = "",
    @SerialName("us-phonetic")
    var usPhonetic: String = "",
    @SerialName("us-speech")
    var usSpeech: String = "",
    @SerialName("wfs")
    var wfs: List<Wf> = listOf()
)
package com.cyy.app.apiTest.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TransRes(
    @SerialName("basic")
    var basic: Basic = Basic(),
    @SerialName("dict")
    var dict: Dict = Dict(),
    @SerialName("errorCode")
    var errorCode: String = "",
    @SerialName("isDomainSupport")
    var isDomainSupport: Boolean = false,
    @SerialName("isWord")
    var isWord: Boolean = false,
    @SerialName("l")
    var l: String = "",
    @SerialName("mTerminalDict")
    var mTerminalDict: MTerminalDict = MTerminalDict(),
    @SerialName("query")
    var query: String = "",
    @SerialName("requestId")
    var requestId: String = "",
    @SerialName("returnPhrase")
    var returnPhrase: List<String> = listOf(),
    @SerialName("speakUrl")
    var speakUrl: String = "",
    @SerialName("tSpeakUrl")
    var tSpeakUrl: String = "",
    @SerialName("translation")
    var translation: List<String> = listOf(),
    @SerialName("web")
    var web: List<Web> = listOf(),
    @SerialName("webdict")
    var webdict: Webdict = Webdict()
)
package com.cyy.transapp.model.trans


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Webdict(
    @SerialName("url")
    var url: String = ""
)
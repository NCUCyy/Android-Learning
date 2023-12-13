package com.cyy.transapp.model.trans


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Dict(
    @SerialName("url")
    var url: String = ""
)
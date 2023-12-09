package com.cyy.app.apiTest.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Wf(
    @SerialName("wf")
    var wf: WfX = WfX()
)
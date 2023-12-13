package com.cyy.transapp.model.trans


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Wf(
    @SerialName("wf")
    var wf: WfX = WfX()
)
package com.cyy.transapp.pojo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ListenResource(
    val topic: String = "",
    val img: Int = 0,
    val mp3: Int = 0,
    val en: Int = 0,
    val zh: Int = 0
) : Parcelable

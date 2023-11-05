package com.cyy.exp2.jump2

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

data class Teacher(val name: String, val gender: String, val workYear: Int) : Serializable
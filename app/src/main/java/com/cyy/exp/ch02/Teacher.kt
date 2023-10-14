package com.cyy.exp.ch02

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Teacher(val name: String, val gender: String, val workYear: Int) : Parcelable {
}
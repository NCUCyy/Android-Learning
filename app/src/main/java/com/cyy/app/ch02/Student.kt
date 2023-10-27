package com.cyy.app.ch02

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Student(val id: String, val name: String, val gender: String) : Parcelable {
}
package com.cyy.exp2.jump2

import android.os.LocaleList
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable
import java.time.LocalDate


data class Student(val name: String, val gender: String, val birth: LocalDate) : Serializable
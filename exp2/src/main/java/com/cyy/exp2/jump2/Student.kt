package com.cyy.exp2.jump2

import android.os.LocaleList
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate


@Parcelize
data class Student(val name: String, val gender: String, val birth: LocalDate) : Parcelable
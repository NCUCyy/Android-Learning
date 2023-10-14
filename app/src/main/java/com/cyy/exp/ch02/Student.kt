package com.cyy.exp.ch02

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable


@Parcelize
data class Student(val id: String, val name: String, val gender: String) : Parcelable {
}
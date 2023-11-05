package com.cyy.app.ch04

import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class User(val time: LocalDateTime)

@RequiresApi(34)
fun main() {
    val user1: User = User(LocalDateTime.now())
//    Thread.sleep(1000)
//    val user2: User = User(LocalDateTime.now())
//    val lst = mutableListOf(user1, user2)
//    lst.sortByDescending { it.time }
//    println(lst)
    println(user1.time.toLocalTime().toString())
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    println(user1.time.format(formatter))
}
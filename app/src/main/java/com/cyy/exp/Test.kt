package com.cyy.exp


fun add(x: Double, y: Double): Double = x + y
data class Student(val name: String, var gender: String)

sealed class Express{
    data class StringExpress(val str:String):Express()
}
fun main() {
    var name: String = "cyy"
    var stu = Student("cyy", "male")
    var stu2 = with(stu) {
        println(this)
        Student("dx","female")
    }
    println(stu2)
}
package com.cyy.exp

fun operate(x: Double, y: Double, op: (Double, Double) -> Unit) {

    op(x, y)

}

fun main() {
    var message: String? = "hello kotlin world"

    val c = message?.also {

        println(it.length)

        println(it.lines())

    }

    println(c)
}

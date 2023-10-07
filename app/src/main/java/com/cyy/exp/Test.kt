package com.cyy.exp

import java.util.Scanner


class CharacterException(override val message: String) : Exception(message) {
    override fun toString(): String = "${message}中包含*或@或￥或#！"
}

fun main() {
    val scan = Scanner(System.`in`)
    val str = scan.next()
    if (str.contains('@') || str.contains('#') || str.contains('$') || str.contains('*'))
        throw CharacterException(str)
    else
        println(str)
}
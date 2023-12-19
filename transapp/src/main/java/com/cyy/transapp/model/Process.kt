package com.cyy.transapp.model

import com.google.gson.Gson

class PlanWord {
    var index: Int = 0
    var process: Int = 0
    var interval: Int = 0

    constructor()
    constructor(index: Int) {
        this.index = index
    }
}

class LearnProcess {
    var process: MutableList<PlanWord> = mutableListOf()
    var learnedIdx: Int = 0
    var learnedNum: Int = 0
}

class ReviewProcess {
    val process: MutableList<PlanWord> = mutableListOf()
}

fun main() {
    val res = Gson().toJson(LearnProcess())
    println(res)
    val res2 = Gson().fromJson(res, LearnProcess::class.java)
    println(res2)
}
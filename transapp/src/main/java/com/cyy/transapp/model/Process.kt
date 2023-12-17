package com.cyy.transapp.model

import com.google.gson.Gson

class PlanWord {
    val index: Int = 0
    val process: Int = 0
    val interval: Int = 0
}

class LearnProcess {
    val process: List<PlanWord> = listOf()
    val learnedIdx: Int = 0
    val learnedNum: Int = 0
}

class ReviewProcess {
    val process: List<PlanWord> = listOf()
}

fun main() {
    val res = Gson().toJson(LearnProcess())
    println(res)
    val res2 = Gson().fromJson(res, LearnProcess::class.java)
    println(res2)
}
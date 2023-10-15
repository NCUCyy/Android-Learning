package com.cyy.exp1.diceGame

enum class GameStatus(val description: String, var point: Int) {
    START("游戏开始", 0),
    WIN("你赢了！", 0),
    LOSE("你输了！", 0),
    GOON("请继续游戏", 0);


    override fun toString(): String = "$description/$point"
    fun updatePoint(point: Int) {
        this.point = point
    }
}
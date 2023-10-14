package com.cyy.exp1.diceGame

enum class GameStatus(val description: String, var point: Int) {
    WIN("你赢了！", 0),
    LOSE("你输了！", 0),
    GOON("请继续游戏", 0);

    override fun toString(): String = description
}
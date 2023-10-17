package com.cyy.exp1.diceGame

enum class GameStatus(val description: String, var point: Int) {
    START("游戏开始", 0),
    WIN("恭喜你胜利啦！！", 0),
    LOSE("再接再厉哟～", 0),
    GOON("请继续游戏", 0);


    override fun toString(): String = "$description"
    fun updatePoint(point: Int) {
        this.point = point
    }
}
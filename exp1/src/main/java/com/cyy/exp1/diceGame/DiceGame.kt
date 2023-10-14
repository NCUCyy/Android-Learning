package com.cyy.exp1.diceGame

import java.util.Random

class DiceGame {
    private val rand = Random()

    // 供活动中调用
    fun rollDice() = rand.nextInt(6) + 1
    fun judgeGame(first: Int, second: Int) = when (first + second) {
        7, 11 -> GameStatus.WIN
        2, 3, 12 -> GameStatus.LOSE
        else -> GameStatus.GOON
    }

    fun goonGame(first: Int, second: Int, status: GameStatus): GameStatus {
        val result = when (val total: Int = first + second) {
            7 -> GameStatus.LOSE
            status.point -> GameStatus.GOON
            else -> {
                status.point = total
                status
            }
        }
        return result
    }
}
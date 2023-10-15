package com.cyy.exp1.diceGame

import java.util.Random

/**
 * 第一次扔：
 * 7, 11 -> 赢
 * 2, 3, 1 -> 输
 * 其他 -> judgeLaterTurn （更新point）
 *
 * 第二/三..次扔：
 * 7 -> 输
 * == status.point -> 赢
 * 其他 -> judgeLaterTurn （更新point）
 */
class DiceGame {
    private val rand = Random()

    // 供活动中调用
    fun rollDice() = rand.nextInt(6) + 1

    // 供活动中调用
    fun judgeFirstTurn(first: Int, second: Int) = when (first + second) {
        7, 11 -> GameStatus.WIN
        2, 3, 12 -> GameStatus.LOSE
        else -> {
            val status = GameStatus.GOON
            status.updatePoint(first + second)
            // {}的结果
            status
        }
    }

    // 供活动中调用
    fun judgeLaterTurn(first: Int, second: Int, status: GameStatus): GameStatus {
        val result = when (val total = first + second) {
            7 -> GameStatus.LOSE
            status.point -> GameStatus.WIN
            else -> {
                status.updatePoint(point = total)
                // {}的结果
                status
            }
        }
        return result
    }
}
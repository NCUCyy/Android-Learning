package com.cyy.transapp.model

import com.cyy.app.word_bank.model.WordItem
import kotlin.random.Random

class QuizWord {
    val word: String = ""
    val answer: String = ""
    val options: List<String> = listOf()

    constructor()

    /**
     * 给定当前Word在所有单词中的索引，构造一个QuizWord
     * 1、ABCD中选择一个作为答案
     * 2、其他的选项从所有单词中随机选取（三次，每次随机获得一个索引，取出对应的WordItm，取第一个translate作为选项）
     * 需要所有单词的列表
     */
    constructor(curIdx: Int, allWords: List<WordItem>) {
        // TODO：构造一个QuizWord
        // 1、ABCD中选择一个作为答案
        val answerIdx = Random.nextInt(0, 4)
        // 2、其他的选项从所有单词中随机选取（三次，每次随机获得一个索引，取出对应的WordItm，取第一个translate作为选项）
        val randomInt = Random.nextInt(0, allWords.size)
    }
}
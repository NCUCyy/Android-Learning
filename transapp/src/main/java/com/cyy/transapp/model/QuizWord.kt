package com.cyy.transapp.model

import com.cyy.app.word_bank.model.WordItem
import kotlin.random.Random

class QuizWord {
    var word: String = ""
    var answer: String = ""
    var options: MutableList<String> = mutableListOf()

    constructor()

    /**
     * 给定当前Word在所有单词中的索引，构造一个QuizWord
     * 1、ABCD中选择一个作为答案
     * 2、其他的选项从所有单词中随机选取（三次，每次随机获得一个索引，取出对应的WordItm，取第一个translate作为选项）
     * 需要所有单词的列表
     */
    constructor(curIdx: Int, allWords: List<WordItem>) {
        // TODO：构造一个QuizWord
        this.word = allWords[curIdx].word
        // 1、ABCD中选择一个作为答案
        val answerIdx = Random.nextInt(0, 4)
        val selectedIdx = mutableListOf<Int>()
        selectedIdx.add(curIdx)
        // 2、其他的选项从所有单词中随机选取（三次，每次随机获得一个索引，取出对应的WordItm，取第一个translate作为选项）
        for (i in 0..3) {
            if (answerIdx == i) {
                // TODO：设置答案
                val translation = allWords[curIdx].translations[0]
                this.answer = "${translation.type}. ${translation.translation}"
                this.options.add(this.answer)
            } else {
                // TODO：设置选项
                var idx: Int
                while (true) {
                    // 四个选项的idx不能重复
                    idx = Random.nextInt(0, allWords.size)
                    if (idx in selectedIdx) {
                        continue
                    } else {
                        selectedIdx.add(idx)
                        break
                    }
                }
                val translation = allWords[idx].translations[0]
                this.options.add("${translation.type}. ${translation.translation}")
            }
        }
    }

}
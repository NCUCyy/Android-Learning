package com.cyy.transapp.repository

import android.app.Activity
import com.cyy.app.word_bank.model.Word
import com.cyy.app.word_bank.model.WordItem
import com.cyy.transapp.model.Vocabulary
import com.cyy.transapp.util.FileUtil
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.serialization.json.Json

class VocabularyRepository {
    val vocabularies =
        listOf(
            Vocabulary.NOT_SELECTED,
            Vocabulary.CHUZHONG,
            Vocabulary.GAOZHONG,
            Vocabulary.CET4,
            Vocabulary.CET6,
            Vocabulary.TOEFL,
            Vocabulary.SAT,
            Vocabulary.KAOYAN
        )

    fun getVocabularyByStr(str: String): Vocabulary =
        when (str) {
            "未选择" -> Vocabulary.NOT_SELECTED
            "CET-4" -> Vocabulary.CET4
            "CET-6" -> Vocabulary.CET6
            "TOEFL" -> Vocabulary.TOEFL
            "SAT" -> Vocabulary.SAT
            "考研" -> Vocabulary.KAOYAN
            "高中" -> Vocabulary.GAOZHONG
            "初中" -> Vocabulary.CHUZHONG
            else -> Vocabulary.NOT_SELECTED
        }

    /**
     * 用这个（比下面那个解析更快！）
     */
    fun getVocabularyWord(context: Activity, vocabulary: Vocabulary): Word {
        val jsonContent = FileUtil.readRawToTxt(context, vocabulary.fileDir)
        return Gson().fromJson(jsonContent, Word::class.java)
    }

    /**
     * Deprecated（解析太慢！）
     */
    fun getVocabularyWords(context: Activity, vocabulary: Vocabulary): List<WordItem> {
        val jsonContent = FileUtil.readRawToTxt(context, vocabulary.fileDir)
        val jsonArray = JsonParser.parseString(jsonContent).asJsonArray
        val wordList = mutableListOf<WordItem>()

        jsonArray.forEach {
            val word = Json.decodeFromJsonElement(
                WordItem.serializer(),
                Json.parseToJsonElement(it.toString())
            )
            wordList.add(word)
        }
        return wordList

//        // 随机获得一个
//        val randomInt = Random.nextInt(0, jsonArray.size())
//        val randomWord = Json.decodeFromJsonElement(
//            WordItem.serializer(),
//            Json.parseToJsonElement(jsonArray[randomInt].asJsonObject.toString())
//        )
//
//        val curDay = 1
//        val curIdx = curDay * 20
//        for (i in curIdx until curIdx + minOf(20, jsonArray.size())) {
//            val jsonObject = jsonArray[i].asJsonObject
//            // TODO：JSON解析的良好解决方案
//            val word = Json.decodeFromJsonElement(
//                WordItem.serializer(),
//                Json.parseToJsonElement(jsonObject.toString())
//            )
//            println(word)
//        }
    }
}
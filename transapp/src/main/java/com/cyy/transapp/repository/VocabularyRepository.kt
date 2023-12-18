package com.cyy.transapp.repository

import android.app.Activity
import com.cyy.app.word_bank.model.WordItem
import com.cyy.transapp.model.Vocabulary
import com.cyy.transapp.util.FileUtil
import com.google.gson.JsonParser
import kotlinx.serialization.json.Json

class VocabularyRepository {
    val vocabularies = listOf(Vocabulary.CET4, Vocabulary.CET6, Vocabulary.TOEFL)


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
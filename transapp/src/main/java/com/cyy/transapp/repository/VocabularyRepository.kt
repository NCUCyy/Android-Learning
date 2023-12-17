package com.cyy.transapp.repository

import com.cyy.app.word_bank.model.WordItem
import com.cyy.transapp.util.FileUtil
import com.google.gson.JsonParser
import kotlinx.serialization.json.Json

class VocabularyRepository {
    /**
     * 把单词本(名称)转换为字符串(JSON字符串)
     */
    private fun transferVocabularyToString(vocabulary: String): String {
        val baseDir =
            "/Users/cyy/AndroidStudioProjects/Chenyi/transapp/src/main/java/com/cyy/transapp/repository/vocabulary/"
        val fileName = baseDir + when (vocabulary) {
            "CET4" -> "CET4.json"
            "CET6" -> "CET6.json"
            "考研" -> "考研.json"
            "SAT" -> "SAT.json"
            "TOEFL" -> "TOEFL.json"
            else -> "CET4.json" // 默认CET4.json
        }
        return FileUtil.readFileAsString(fileName)
    }

    fun getVocabularyWords(vocabulary: String): List<WordItem> {
        val jsonContent = transferVocabularyToString(vocabulary)
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
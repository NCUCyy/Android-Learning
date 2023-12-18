package com.cyy.transapp.repository

import com.cyy.app.word_bank.model.WordItem
import com.cyy.transapp.model.Vocabulary
import com.cyy.transapp.util.FileUtil
import com.google.gson.JsonParser
import kotlinx.serialization.json.Json

class VocabularyRepository {
    val vocabularies = listOf(Vocabulary.CET4, Vocabulary.CET6, Vocabulary.TOEFL)

    /**
     * TODO：弃用——因为：Vocabulary类的使用
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

    fun getVocabularyWords(vocabulary: Vocabulary): List<WordItem> {
//        val jsonContent = transferVocabularyToString(vocabulary.desc)
        val jsonContent = FileUtil.readFileAsString(vocabulary.fileDir)
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
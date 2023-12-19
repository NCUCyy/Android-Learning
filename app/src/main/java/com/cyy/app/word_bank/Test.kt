import com.cyy.app.word_bank.model.Word
import com.cyy.app.word_bank.model.WordItem
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.random.Random

fun readFileAsString(filePath: String): String {
    return File(filePath).readText(Charsets.UTF_8)
}

fun parseFirstTwenty(json: String) {
    val jsonArray = JsonParser.parseString(json).asJsonArray
    // 随机获得一个
    val randomInt = Random.nextInt(0, jsonArray.size())
    val randomWord = Json.decodeFromJsonElement(
        WordItem.serializer(),
        Json.parseToJsonElement(jsonArray[randomInt].asJsonObject.toString())
    )
    println("random=====>${randomWord.translations[0].type}.${randomWord.translations[0].translation}")
    println("------------------------")
    val curDay = 1
    val curIdx = curDay * 20
    for (i in curIdx until curIdx + minOf(20, jsonArray.size())) {
        val jsonObject = jsonArray[i].asJsonObject
        // TODO：JSON解析的良好解决方案
        val word = Json.decodeFromJsonElement(
            WordItem.serializer(),
            Json.parseToJsonElement(jsonObject.toString())
        )
        println(word)
    }
}

fun test(json: String): Word {
//    val jsonObject = JsonParser.parseString(json).asJsonObject
//    return Json.decodeFromJsonElement(
//        Word.serializer(),
//        Json.parseToJsonElement(
//            jsonObject.toString()
//        )
//    )
    return  Gson().fromJson(json, Word::class.java)
}

fun main() {
    val jsonContent =
        readFileAsString("/Users/cyy/AndroidStudioProjects/Chenyi/app/src/main/java/com/cyy/app/word_bank/bank2/考研.json")
//    parseFirstTwenty(jsonContent)
    println(test(jsonContent))
}
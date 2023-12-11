import com.cyy.app.word_bank.model.WordItem
import com.google.gson.JsonParser
import kotlinx.serialization.json.Json
import java.io.File

fun readFileAsString(filePath: String): String {
    return File(filePath).readText(Charsets.UTF_8)
}

fun parseFirstTwenty(json: String) {
    val jsonArray = JsonParser.parseString(json).asJsonArray
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

fun main() {
    val jsonContent =
        readFileAsString("/Users/cyy/AndroidStudioProjects/Chenyi/app/src/main/java/com/cyy/app/word_bank/bank2/考研.json")
    parseFirstTwenty(jsonContent)
}
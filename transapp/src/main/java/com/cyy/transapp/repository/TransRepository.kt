package com.cyy.transapp.repository

import com.cyy.transapp.model.OpResult
import com.cyy.transapp.model.trans.TransRes
import com.cyy.transapp.network.AuthV3Util
import com.cyy.transapp.network.HttpUtil
import kotlinx.serialization.json.Json
import java.nio.charset.StandardCharsets

class TransRepository {
    private val APP_KEY = "6c2ee08f4ca04995" // 您的应用ID
    private val APP_SECRET = "VICd82fdy7GVQ3fYKcbXwFFgWEc7qsE8" // 您的应用密钥

    fun translate(
        query: String,
        callBack: (OpResult<Any>) -> Unit
    ) {
        // 添加请求参数
        val params = createRequestParams(query)
        // 添加鉴权相关参数
        AuthV3Util.addAuthParams(
            APP_KEY,
            APP_SECRET,
            params as MutableMap<String?, Array<String?>?>
        )
        // 请求api服务
        val result: ByteArray? =
            HttpUtil.doPost("https://openapi.youdao.com/api", null, params, "application/json")
        if (result == null)
            callBack(OpResult.Error("翻译失败！请检查网络设置 😵"))
        else {
            val res = Json.decodeFromJsonElement(
                TransRes.serializer(),
                Json.parseToJsonElement(String(result, StandardCharsets.UTF_8))
            )
            callBack(OpResult.Success(res))
        }
    }

    private fun createRequestParams(query: String): Map<String, Array<String>>? {
        /*
         * note: 将下列变量替换为需要请求的参数
         * 取值参考文档: https://ai.youdao.com/DOCSIRMA/html/%E8%87%AA%E7%84%B6%E8%AF%AD%E8%A8%80%E7%BF%BB%E8%AF%91/API%E6%96%87%E6%A1%A3/%E6%96%87%E6%9C%AC%E7%BF%BB%E8%AF%91%E6%9C%8D%E5%8A%A1/%E6%96%87%E6%9C%AC%E7%BF%BB%E8%AF%91%E6%9C%8D%E5%8A%A1-API%E6%96%87%E6%A1%A3.html
         */
        val q = query
        val from = "en"
        val to = "zh"
        val vocabId = "computers"
        return object : HashMap<String, Array<String>>() {
            init {
                put("q", arrayOf(q))
                put("from", arrayOf(from))
                put("to", arrayOf(to))
                put("vocabId", arrayOf(vocabId))
            }
        }
    }
}

package com.cyy.transapp.network

import com.cyy.transapp.model.trans.TransRes
import kotlinx.serialization.json.Json
import java.nio.charset.StandardCharsets
import java.security.NoSuchAlgorithmException


/**
 * 网易有道智云翻译服务api调用demo
 * api接口: https://openapi.youdao.com/api
 */
object TranslateDemo {
    private const val APP_KEY = "6c2ee08f4ca04995" // 您的应用ID
    private const val APP_SECRET = "VICd82fdy7GVQ3fYKcbXwFFgWEc7qsE8" // 您的应用密钥

    @Throws(NoSuchAlgorithmException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        // 添加请求参数
        val params = createRequestParams()
        // 添加鉴权相关参数
        AuthV3Util.addAuthParams(APP_KEY, APP_SECRET, params as MutableMap<String?, Array<String?>?>)
        // 请求api服务
        val result: ByteArray? =
            HttpUtil.doPost("https://openapi.youdao.com/api", null, params, "application/json")
        // 打印返回结果
        if (result != null) {
            println(String(result, StandardCharsets.UTF_8))
        }
        // TODO：JSON解析较好的解决方案————没有值的属性为[]
        val res = Json.decodeFromJsonElement(TransRes.serializer(), Json.parseToJsonElement(String(result!!, StandardCharsets.UTF_8)))
        println(res)
        System.exit(1)
    }

    private fun createRequestParams(): Map<String, Array<String>>? {
        /*
         * note: 将下列变量替换为需要请求的参数
         * 取值参考文档: https://ai.youdao.com/DOCSIRMA/html/%E8%87%AA%E7%84%B6%E8%AF%AD%E8%A8%80%E7%BF%BB%E8%AF%91/API%E6%96%87%E6%A1%A3/%E6%96%87%E6%9C%AC%E7%BF%BB%E8%AF%91%E6%9C%8D%E5%8A%A1/%E6%96%87%E6%9C%AC%E7%BF%BB%E8%AF%91%E6%9C%8D%E5%8A%A1-API%E6%96%87%E6%A1%A3.html
         */
        val q = "authentic"
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

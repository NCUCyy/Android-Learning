package com.cyy.transapp.network

import okhttp3.FormBody
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.nio.charset.StandardCharsets

object HttpUtil {
    private val httpClient = OkHttpClient.Builder().build()

    fun doGet(url: String, header: Map<String, Array<String>>?, params: Map<String, Array<String>>?, expectContentType: String): ByteArray? {
        val builder = Request.Builder()
        addHeader(builder, header)
        addUrlParam(builder, url, params)
        return requestExec(builder.build(), expectContentType)
    }

    fun doPost(url: String, header: Map<String, Array<String>>?, body: Map<String, Array<String>>?, expectContentType: String): ByteArray? {
        val builder = Request.Builder().url(url)
        addHeader(builder, header)
        addBodyParam(builder, body, "POST")
        return requestExec(builder.build(), expectContentType)
    }

    private fun addHeader(builder: Request.Builder, header: Map<String, Array<String>>?) {
        header?.forEach { (key, values) ->
            values?.forEach { value ->
                builder.addHeader(key, value)
            }
        }
    }

    private fun addUrlParam(builder: Request.Builder, url: String, params: Map<String, Array<String>>?) {
        val urlBuilder = HttpUrl.Builder().scheme("http").host(url)
        params?.forEach { (key, values) ->
            values?.forEach { value ->
                urlBuilder.addQueryParameter(key, value)
            }
        }
        builder.url(urlBuilder.build())
    }

    private fun addBodyParam(builder: Request.Builder, body: Map<String, Array<String>>?, method: String) {
        val formBodyBuilder = FormBody.Builder(StandardCharsets.UTF_8)
        body?.forEach { (key, values) ->
            values?.forEach { value ->
                formBodyBuilder.add(key, value)
            }
        }
        builder.method(method, formBodyBuilder.build())
    }

    private fun requestExec(request: Request, expectContentType: String): ByteArray? {
        requireNotNull(request) { "okHttp request is null" }

        try {
            httpClient.newCall(request).execute().use { response:Response ->
                if (response.code == 200) {
                    val body = response.body
                    if (body != null) {
                        val contentType = response.header("Content-Type")
                        if (contentType != null && !contentType.contains(expectContentType)) {
                            val res = String(body.bytes(), StandardCharsets.UTF_8)
                            println(res)
                            return null
                        }
                        return body.bytes()
                    }
                    println("response body is null")
                } else {
                    println("request failed, http code: ${response.code}")
                }
            }
        } catch (ioException: IOException) {
            println("request exec error: ${ioException.message}")
        }
        return null
    }
}

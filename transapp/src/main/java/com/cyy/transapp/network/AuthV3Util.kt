package com.cyy.transapp.network

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.UUID

object AuthV3Util {
    /**
     * 添加鉴权相关参数 -
     * appKey : 应用ID
     * salt : 随机值
     * curtime : 当前时间戳(秒)
     * signType : 签名版本
     * sign : 请求签名
     *
     * @param appKey    您的应用ID
     * @param appSecret 您的应用密钥
     * @param paramsMap 请求参数表
     */
    @Throws(NoSuchAlgorithmException::class)
    fun addAuthParams(
        appKey: String,
        appSecret: String,
        paramsMap: MutableMap<String?, Array<String?>?>
    ) {
        var qArray = paramsMap["q"]
        if (qArray == null) {
            qArray = paramsMap["img"]
        }
        val q = StringBuilder()
        for (item in qArray!!) {
            q.append(item)
        }
        val salt = UUID.randomUUID().toString()
        val curtime = (System.currentTimeMillis() / 1000).toString()
        val sign = calculateSign(appKey, appSecret, q.toString(), salt, curtime)
        paramsMap["appKey"] = arrayOf(appKey)
        paramsMap["salt"] = arrayOf(salt)
        paramsMap["curtime"] = arrayOf(curtime)
        paramsMap["signType"] = arrayOf("v3")
        paramsMap["sign"] = arrayOf(sign)
    }

    /**
     * 计算鉴权签名 -
     * 计算方式 : sign = sha256(appKey + input(q) + salt + curtime + appSecret)
     *
     * @param appKey    您的应用ID
     * @param appSecret 您的应用密钥
     * @param q         请求内容
     * @param salt      随机值
     * @param curtime   当前时间戳(秒)
     * @return 鉴权签名sign
     */
    @Throws(NoSuchAlgorithmException::class)
    fun calculateSign(
        appKey: String,
        appSecret: String,
        q: String?,
        salt: String,
        curtime: String
    ): String {
        val strSrc = appKey + getInput(q) + salt + curtime + appSecret
        return encrypt(strSrc)
    }

    @Throws(NoSuchAlgorithmException::class)
    private fun encrypt(strSrc: String): String {
        val bt = strSrc.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        md.update(bt)
        val bts = md.digest()
        val des = StringBuilder()
        for (b in bts) {
            val tmp = Integer.toHexString(b.toInt() and 0xFF)
            if (tmp.length == 1) {
                des.append("0")
            }
            des.append(tmp)
        }
        return des.toString()
    }

    private fun getInput(input: String?): String? {
        if (input == null) {
            return null
        }
        val result: String
        val len = input.length
        result = if (len <= 20) {
            input
        } else {
            val startStr = input.substring(0, 10)
            val endStr = input.substring(len - 10, len)
            startStr + len + endStr
        }
        return result
    }
}
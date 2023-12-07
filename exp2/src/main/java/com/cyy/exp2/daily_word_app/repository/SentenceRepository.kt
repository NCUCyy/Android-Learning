package com.cyy.exp2.daily_word_app.repository

import com.cyy.exp2.Model_Handler_Pool_Net.Result2
import com.cyy.exp2.daily_word_app.model.OpResult
import com.cyy.exp2.daily_word_app.model.SentenceModel
import com.cyy.exp2.daily_word_app.network.SerializationConverter
import com.drake.net.Get
import com.drake.net.utils.scopeNet


class SentenceRepository {
    /**
     * 请求加载数据
     * @param callBack Function1<Result2<String>, Unit> 回调
     */
    fun requestSentence(
        callBack: (OpResult<Any>) -> Unit
    ) {
        var sentenceModel: SentenceModel
        //线程池中创建一个新线程并执行
        // 开启协程
        scopeNet {
            // 若抛出异常，则sentenceModel不会被赋任何值
            sentenceModel = Get<SentenceModel>("https://api.vvhan.com/api/en?type=sj") {
                converter = SerializationConverter()
            }.await()
            // TODO：成功！按照列表索引请求图片资源~
            val successResult2 = OpResult.Success(sentenceModel)
            // 与主线程通信
            callBack(successResult2)
        }.catch {
            it
            // TODO：失败！显示错误信息~
            val errorResult2 = OpResult.Error("加载失败！请检查网络设置 😵")
            callBack(errorResult2)
        }
    }
}
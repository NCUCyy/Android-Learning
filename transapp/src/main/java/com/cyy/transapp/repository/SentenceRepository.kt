package com.cyy.transapp.repository

import com.cyy.transapp.model.OpResult
import com.cyy.transapp.model.daily_sentence.SentenceModel
import com.cyy.transapp.network.SerializationConverter
import com.drake.net.Get
import com.drake.net.utils.scopeNet

/**
 * 查询模块的句子仓库---【每日一句】
 */
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
            sentenceModel = Get<SentenceModel>("https://api.vvhan.com/api/en") {
                converter = SerializationConverter()
            }.await()
            // TODO：成功！按照列表索引请求图片资源~
            val successResult = OpResult.Success(sentenceModel)
            // 与主线程通信
            callBack(successResult)
        }.catch {
            it
            // TODO：失败！显示错误信息~
            val errorResult = OpResult.Error("请检查网络配置后重试！")
            callBack(errorResult)
        }
    }
}
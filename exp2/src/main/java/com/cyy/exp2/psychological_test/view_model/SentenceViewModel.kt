package com.cyy.exp2.psychological_test.view_model

import androidx.lifecycle.ViewModel
import com.cyy.exp2.psychological_test.model.SentenceModel
import com.cyy.exp2.psychological_test.network.SerializationConverter
import com.drake.net.Get
import com.drake.net.utils.scopeNet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SentenceViewModel : ViewModel() {
    private var _sentence = MutableStateFlow(SentenceModel())
    var sentence = _sentence.asStateFlow()

    init {
        shuffleSentence()
    }

    fun shuffleSentence() {
        scopeNet {
            _sentence.value = Get<SentenceModel>("https://api.vvhan.com/api/en?type=sj") {
                converter = SerializationConverter()
            }.await()
        }
    }
}

//class SenSentenceViewModel() : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(SentenceViewModel::class.java)) {
//            @Suppress("UNCHECKED_CAST")
//            return SentenceViewModel() as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}
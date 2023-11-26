package com.cyy.exp2.psychological_test.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cyy.exp2.psychological_test.repository.QuizRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class QuizViewModel(private val quizRepository: QuizRepository) : ViewModel() {
    val quizzes = quizRepository.quizzes

    // 创建一个列表，用于存储用户选择的答案（大小为问题的个数，初始化为""空串）
    private val _selected = MutableStateFlow(MutableList(quizRepository.quizzes.size) { "" })
    val selected = _selected.asStateFlow()

    // 当前的题目的编号（用于显示：1/20）
    private val _curQuizIdx = MutableStateFlow(0)
    val curQuizIdx = _curQuizIdx.asStateFlow()

    // 当前的题目---由idx计算获得（用于显示：题目和选项）
    private val _curQuiz = MutableStateFlow(quizzes[_curQuizIdx.value])
    val curQuiz = _curQuiz.asStateFlow()

    // 当前的选项
    private val _curOption = MutableStateFlow("")
    val curOption = _curOption.asStateFlow()

    // 总分
    val score = MutableLiveData(0)

    fun setCurQuizIdx(idx: Int) {
        _curQuizIdx.value = idx
        _curQuiz.value = quizzes[_curQuizIdx.value]
        _curOption.value = _selected.value[_curQuizIdx.value]
    }


    fun setOption(option: String) {
        // 只有第一次的选择才有效
        if (_selected.value[_curQuizIdx.value] == "") {
            _curOption.value = option
            _selected.value[_curQuizIdx.value] = option
        }
    }

    fun nextQuiz() {
        setCurQuizIdx(_curQuizIdx.value + 1)
    }

    fun lastQuiz() {
        setCurQuizIdx(_curQuizIdx.value - 1)
    }

    fun commit() {
//        var singleScore = 100 / quizRepository.quizzes.size
        var sum = 0
        _selected.value.forEachIndexed { idx, option ->
            if (option == quizzes[idx].answer) {
                sum++
            }
        }
        score.value = sum
    }

    fun reset() {
        _selected.value = MutableList(quizRepository.quizzes.size) { "" }
        _curQuizIdx.value = 0
        _curQuiz.value = quizzes[_curQuizIdx.value]
        _curOption.value = _selected.value[_curQuizIdx.value]
        score.value = 0
    }
}

class QuizViewModelFactory(private val quizRepository: QuizRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuizViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QuizViewModel(quizRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
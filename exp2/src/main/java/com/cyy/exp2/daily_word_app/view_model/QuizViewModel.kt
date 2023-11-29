package com.cyy.exp2.daily_word_app.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cyy.exp2.daily_word_app.repository.QuizRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.Duration
import java.time.Instant

class QuizViewModel(
    private val quizRepository: QuizRepository,
    val category: String, // 本次的测试题库
    val username: String // 答题人
) :
    ViewModel() {
    // 当前的题库
    val quizzes = quizRepository.getQuiz(category)

    // 开始时间
    private val beginTime = System.currentTimeMillis()

    // 结束时间
    private var endTime = 0L

    // 创建一个列表，用于存储用户选择的答案（大小为问题的个数，初始化为""空串）
    private val _selected = MutableStateFlow(MutableList(quizzes.size) { "" })
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


    private val _curRecord: MutableStateFlow<Record?> = MutableStateFlow(null)
    val curRecord = _curRecord.asStateFlow()

    // 用于结果界面展示
    private val _right = MutableStateFlow(0)
    private val _undo = MutableStateFlow(quizzes.size)
    private val _wrong = MutableStateFlow(0)
    val right = _right.asStateFlow()
    val undo = _undo.asStateFlow()
    val wrong = _wrong.asStateFlow()


    private fun getFormatDuration(): String {
        val instant1 = Instant.ofEpochMilli(beginTime)
        val instant2 = Instant.ofEpochMilli(endTime)
        val duration = Duration.between(instant1, instant2)

        val minutes = duration.toMinutesPart()
        val seconds = duration.toSecondsPart()

        return String.format("%02d:%02d", minutes, seconds)
    }

    fun getTestDuration(): String {
        endTime = System.currentTimeMillis()
        return getFormatDuration()
    }

    fun setCurRecord(record: Record) {
        _curRecord.value = record
    }

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
            // 每次做完题同步修改这三个值
            if (option == quizzes[_curQuizIdx.value].answer) {
                _right.value++
            } else {
                _wrong.value++
            }
            _undo.value--
        }
    }

    fun nextQuiz() {
        setCurQuizIdx(_curQuizIdx.value + 1)
    }

    fun lastQuiz() {
        setCurQuizIdx(_curQuizIdx.value - 1)
    }

//    TODO：不用这个了，因为在做题的时候已经同步计算了right值
//    fun commit() {
////        var singleScore = 100 / quizRepository.quizzes.size
//        var sum = 0
//        _selected.value.forEachIndexed { idx, option ->
//            if (option == quizzes[idx].answer) {
//                sum++
//            }
//        }
//        score.value = sum
//    }

    fun reset() {
        _selected.value = MutableList(quizRepository.quizzes.size) { "" }
        _curQuizIdx.value = 0
        _curQuiz.value = quizzes[_curQuizIdx.value]
        _curOption.value = _selected.value[_curQuizIdx.value]
        _right.value = 0
        _undo.value = quizzes.size
        _wrong.value = 0
    }
}

class QuizViewModelFactory(
    private val quizRepository: QuizRepository,
    private val category: String,
    private val username: String
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuizViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QuizViewModel(quizRepository, category, username) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
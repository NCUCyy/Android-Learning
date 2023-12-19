package com.cyy.transapp.view_model

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cyy.app.word_bank.model.Word
import com.cyy.app.word_bank.model.WordItem
import com.cyy.transapp.model.LearnProcess
import com.cyy.transapp.model.OpResult
import com.cyy.transapp.model.PlanWord
import com.cyy.transapp.model.QuizWord
import com.cyy.transapp.model.Vocabulary
import com.cyy.transapp.pojo.Plan
import com.cyy.transapp.pojo.User
import com.cyy.transapp.repository.PlanRepository
import com.cyy.transapp.repository.TodayRepository
import com.cyy.transapp.repository.TransRepository
import com.cyy.transapp.repository.UserRepository
import com.cyy.transapp.repository.VocabularyRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.concurrent.thread

class LearnViewModel(
    private val userId: Int,
    private val vocabulary: String,
    private val context: Activity,
    private val userRepository: UserRepository,
    private val todayRepository: TodayRepository,
    private val planRepository: PlanRepository,
    private val transRepository: TransRepository,
    private val vocabularyRepository: VocabularyRepository,
    private val words: List<WordItem>,
) : ViewModel() {
    // 当前登录的用户---用于观察
    val curUser = userRepository.getFlowById(userId).stateIn(
        initialValue = User(),
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(0)
    )

    // 当前的学习计划---用户观察
    val plan = planRepository.getFlowByUserIdAndVocabulary(userId, vocabulary).stateIn(
        initialValue = Plan(),
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(0)
    )
    private val _curWord = MutableStateFlow(QuizWord())
    val curWord = _curWord.asStateFlow()

    private val _loadVocabularyState = MutableStateFlow<OpResult<Any>>(OpResult.NotBegin)
    val loadVocabularyState: StateFlow<OpResult<Any>> = _loadVocabularyState.asStateFlow()

    private lateinit var allWords: Word

    init {
        _loadVocabularyState.value = OpResult.Loading
        thread {
            allWords =
                vocabularyRepository.getVocabularyWord(context, Vocabulary.valueOf(vocabulary))
            _loadVocabularyState.value = OpResult.Success("加载成功！")
        }
    }

    fun nextWord() = viewModelScope.launch {
        val plan = planRepository.getByUserIdAndVocabulary(userId, vocabulary)
        val learnProcess = Gson().fromJson(plan.learnProcess, LearnProcess::class.java)
        // 构造出一个
        if (learnProcess.process.size > 0) {
            val curIdx = getNextIdx(learnProcess.process)
            _curWord.value = QuizWord()
        }
    }

    private fun getNextIdx(process: List<PlanWord>): Int {
        // TODO:Algorithm---选process/(1 + interval)最小的那个Word
        if (process.size == 1) {
            return 0
        }
        var selected = 0
        var curMin = 0f
        for (i in 1..process.size) {
            val tmp = process[i].process / (process[i].interval + 1).toFloat()
            if (tmp < curMin) {
                selected = i
                curMin = tmp
            }
        }
        return selected
    }
}

class LearnViewModelFactory(
    private val userId: Int,
    private val vocabulary: String,
    private val context: Activity,
    private val userRepository: UserRepository,
    private val todayRepository: TodayRepository,
    private val planRepository: PlanRepository,
    private val transRepository: TransRepository,
    private val vocabularyRepository: VocabularyRepository,
    private val words: List<WordItem>
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LearnViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LearnViewModel(
                userId,
                vocabulary,
                context,
                userRepository,
                todayRepository,
                planRepository,
                transRepository,
                vocabularyRepository,
                words
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

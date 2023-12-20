package com.cyy.transapp.view_model

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cyy.app.word_bank.model.Word
import com.cyy.transapp.model.LearnProcess
import com.cyy.transapp.model.OpResult
import com.cyy.transapp.model.PlanWord
import com.cyy.transapp.model.QuizWord
import com.cyy.transapp.model.ReviewProcess
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

    // 当前选择的单词的索引（在LearnProcess的process列表中的索引）
    private val _curIdx = MutableStateFlow(0)
    val curIdx = _curIdx.asStateFlow()

    // 当前选择的————PlanWord
    private val _curPlanWord = MutableStateFlow(PlanWord())
    val curPlanWord = _curPlanWord.asStateFlow()

    // 当前的题目————QuizWord
    private val _curQuizWord = MutableStateFlow(QuizWord())
    val curQuizWord = _curQuizWord.asStateFlow()

    // 当前的选择（用户的）
    private val _curOption = MutableStateFlow("")
    val curOption = _curOption.asStateFlow()

    private val _curWordProcess = MutableStateFlow(0)
    val curWordProcess = _curWordProcess.asStateFlow()

    // 字典的加载状态
    private val _loadVocabularyState = MutableStateFlow<OpResult<Any>>(OpResult.NotBegin)
    val loadVocabularyState: StateFlow<OpResult<Any>> = _loadVocabularyState.asStateFlow()

    private lateinit var allWords: Word

    init {
        _loadVocabularyState.value = OpResult.Loading
        thread {
            allWords =
                vocabularyRepository.getVocabularyWord(context, Vocabulary.valueOf(vocabulary))
            _loadVocabularyState.value = OpResult.Success("加载成功！")
            nextWord()
        }
    }

    fun nextWord() = viewModelScope.launch {
        // 每次从数据库中取出最新的值
        val plan = planRepository.getByUserIdAndVocabulary(userId, vocabulary)
        val learnProcess = Gson().fromJson(plan.learnProcess, LearnProcess::class.java)
        // 构造出一个
        if (learnProcess.process.size > 0) {
            // 若还有词，则构造一个QuizWord
            // 选中最佳的索引(在LearnProcess的process列表中的索引)
            _curIdx.value = getNextIdx(learnProcess.process)
            // 更新所有的interval
            updateInterval(plan, learnProcess)
            _curPlanWord.value = learnProcess.process[_curIdx.value]
            // 取出在总字典中的索引，根据这个idx，构造出一个QuizWord（随机）
            _curQuizWord.value = QuizWord(_curPlanWord.value.index, allWords)
            // 清空原来的选择
            _curOption.value = ""
            // 当前PlanWord的process
            _curWordProcess.value = _curPlanWord.value.process
        } else {
            // 否则，不变
        }
    }

    /**
     * 更新所有LearnProcess中PlanWord的interval
     */
    private fun updateInterval(plan: Plan, learnProcess: LearnProcess) {
        for (i in 0 until learnProcess.process.size) {
            if (i == _curIdx.value) {
                learnProcess.process[i].interval = 0
            } else {
                learnProcess.process[i].interval++
            }
        }
        updatePlanByLearnProcess(plan, learnProcess)
    }

    /**
     * 算法：得到下一个最优下标
     */
    private fun getNextIdx(process: List<PlanWord>): Int {
        // TODO:Algorithm---选process/(1 + interval)最小的那个Word
        if (process.size == 1) {
            return 0
        }
        var selected = 0
        var curMin = (process[0].process + 13) / (process[0].interval + 17).toFloat()
        for (i in 1 until process.size) {
            val tmp = process[i].process / (process[i].interval + 1).toFloat()
            if (tmp < curMin) {
                selected = i
                curMin = tmp
            }
        }
        return selected
    }

    /**
     * 设置用户的选项
     */
    fun setCurOption(option: String) = viewModelScope.launch {
        _curOption.value = option
        val plan = planRepository.getByUserIdAndVocabulary(userId, vocabulary)
        val learnProcess = Gson().fromJson(plan.learnProcess, LearnProcess::class.java)!!
        if (option == _curQuizWord.value.answer) {
            // 答对：process+1
            learnProcess.process[_curIdx.value].process += 1
        } else {
            // 答错：process清空
            learnProcess.process[_curPlanWord.value.index].process = 0
        }
        _curWordProcess.value = learnProcess.process[_curIdx.value].process
        if (learnProcess.process[_curIdx.value].process >= 3) {
            // 若process>=3，则从process中删除
            val reviewProcess = Gson().fromJson(plan.reviewProcess, ReviewProcess::class.java)!!
            learnProcess.process.removeAt(_curIdx.value)
            learnProcess.learnedNum++
            reviewProcess.process.add(_curPlanWord.value)
            updatePlanByReviewProcess(plan, reviewProcess)
        }
        // 更新数据库中的plan
        updatePlanByLearnProcess(plan, learnProcess)
    }

    /**
     * 根据LearnProcess更新Plan
     */
    private fun updatePlanByLearnProcess(plan: Plan, learnProcess: LearnProcess) =
        viewModelScope.launch {
            plan.learnProcess = Gson().toJson(learnProcess)
            planRepository.update(plan)
        }

    /**
     * 根据ReviewProcess更新Plan
     */
    private fun updatePlanByReviewProcess(plan: Plan, reviewProcess: ReviewProcess) =
        viewModelScope.launch {
            plan.reviewProcess = Gson().toJson(reviewProcess)
            planRepository.update(plan)
        }
    /**
     * 选中NextWord，更新所有的interval
     * 选中一个Option后，更新它的process（>=3了要从LearnProcess.process中删掉，加入ReviewProcess.process中）
     */
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
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

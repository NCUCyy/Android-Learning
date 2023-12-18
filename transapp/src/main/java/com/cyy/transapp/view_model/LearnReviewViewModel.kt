package com.cyy.transapp.view_model

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cyy.app.word_bank.model.WordItem
import com.cyy.transapp.model.LearnProcess
import com.cyy.transapp.model.PlanWord
import com.cyy.transapp.model.ReviewProcess
import com.cyy.transapp.model.Vocabulary
import com.cyy.transapp.pojo.Plan
import com.cyy.transapp.pojo.Today
import com.cyy.transapp.pojo.User
import com.cyy.transapp.repository.PlanRepository
import com.cyy.transapp.repository.TodayRepository
import com.cyy.transapp.repository.UserRepository
import com.cyy.transapp.repository.VocabularyRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.OffsetDateTime

/**
 * 保存当前登录的用户---curUser（根据UserId查询得到，ViewModel初始化的时候得到）
 */
class LearnReviewViewModel(
    private val userId: Int,
    private val context: Activity,
    private val userRepository: UserRepository,
    private val todayRepository: TodayRepository,
    private val planRepository: PlanRepository,
    private val vocabularyRepository: VocabularyRepository
) :
    ViewModel() {
    private val now: OffsetDateTime = OffsetDateTime.now()

    // 当前登录的用户
    var curUser = userRepository.getById(userId).stateIn(
        initialValue = User(),
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000)
    )

    // Today---用stateIn可以省略修改后，查询+赋值的两步
    private val _today = MutableStateFlow(Today())
    var today: StateFlow<Today> = _today.asStateFlow()

    // Plan
    private val _plan = MutableStateFlow(Plan())
    var plan: StateFlow<Plan> = _plan.asStateFlow()

    private val _totalWords = MutableStateFlow(listOf<WordItem>())
    val totalWords = _totalWords.asStateFlow()

    init {
        // TODO：②初始化today
        viewModelScope.launch {
            val today =
                todayRepository.getByUserIdAndYMD(
                    userId,
                    now.year,
                    now.monthValue,
                    now.dayOfMonth
                )
            if (today != null) {
                _today.value = today
                _today.value.openNum += 1
                updateToday()
            } else {
                todayRepository.insert(Today(userId))
                _today.value =
                    todayRepository.getByUserIdAndYMD(
                        userId,
                        now.year,
                        now.monthValue,
                        now.dayOfMonth
                    )
            }
        }
    }

    private fun updateToday() = viewModelScope.launch {
        // 更新后再查询一次
        todayRepository.update(_today.value)
        _today.value = todayRepository.getByUserIdAndYMD(
            userId,
            now.year,
            now.monthValue,
            now.dayOfMonth
        )
    }

    // 外部调用
    fun updateVocabulary(vocabulary: Vocabulary) = viewModelScope.launch {
        // 修改user中的vocabulary
        curUser.value.vocabulary = vocabulary.desc
        userRepository.update(curUser.value)
        // 修改当前的词库
        _totalWords.value = vocabularyRepository.getVocabularyWords(context, vocabulary)
        // 修改Plan（有则赋值，没有则插入后赋值）
        updatePlan()
    }

    private fun updatePlan() = viewModelScope.launch {
        val plan = planRepository.getByUserIdAndVocabulary(userId, curUser.value.vocabulary)
        if (plan != null) {
            _plan.value = plan
        } else {
            // 先插进去
            planRepository.insert(Plan(userId, curUser.value.vocabulary))
            // 再查出来（这样LearnProcess和ReviewProcess就有值了————虽然也是空的）
            _plan.value = planRepository.getByUserIdAndVocabulary(userId, curUser.value.vocabulary)
            initLearnProcess()
        }
    }

    private fun initLearnProcess() {
        val learnProcess = getLearnProcess()
        // 初始化dailyNum个词汇
        for (i in 0.._plan.value.dailyNum) {
            learnProcess.process.add(PlanWord(i))
        }
        setLearnProcess(learnProcess)
    }

    fun setLearnProcess(learnProcess: LearnProcess) {
        _plan.value.learnProcess = Gson().toJson(learnProcess)
        updatePlan()
    }

    fun getLearnProcess(): LearnProcess {
        // TODO：要确保Plan不为Plan()
        val learnProcessStr = _plan.value.learnProcess
        return Gson().fromJson(learnProcessStr, LearnProcess::class.java)
    }

    fun getReviewProcess(): ReviewProcess {
        // TODO：要确保Plan不为Plan()
        val reviewProcessStr = _plan.value.reviewProcess
        return Gson().fromJson(reviewProcessStr, ReviewProcess::class.java)
    }
}

class LearnReviewViewModelFactory(
    private val userId: Int,
    private val context: Activity,
    private val userRepository: UserRepository,
    private val todayRepository: TodayRepository,
    private val planRepository: PlanRepository,
    private val vocabularyRepository: VocabularyRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LearnReviewViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LearnReviewViewModel(
                userId,
                context,
                userRepository,
                todayRepository,
                planRepository,
                vocabularyRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

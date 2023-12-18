package com.cyy.transapp.view_model

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cyy.transapp.model.LearnProcess
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

    // 当前登录的用户---用于观察
    var curUser: StateFlow<User> = MutableStateFlow(User())

    // Today---用于观察
    val today = todayRepository.getFlowByUserIdAndYMD(
        userId,
        now.year,
        now.monthValue,
        now.dayOfMonth
    ).stateIn(
        initialValue = Today(),
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000)
    )

    // 出勤天数---用于观察
    val todays = todayRepository.getFlowByUserId(userId).stateIn(
        initialValue = listOf(),
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000)
    )

    // Plan---用于观察
    // 每个user固定都有一个空的 Plan=> ()
    var plan: StateFlow<Plan> = MutableStateFlow(Plan())


    private var _vocabularySize = MutableStateFlow(0)
    val vocabularySize: StateFlow<Int> = _vocabularySize.asStateFlow()

    /**
     * 修改单词本之后，要改哪些地方：
     * 1、plan：先去room中查找，找到赋值；没找到创建后查找，再赋值（创建需要给LearnProcess赋初值）
     * 2、user的vocabulary直接修改--->update(user)
     * 3、totalWords：根据vocabulary去room中查找，找到赋值；没找到创建后查找，再赋值
     */
    init {
        // 最早执行的部分（且只执行一次）
        viewModelScope.launch {
            // 三个初始化部分先后---StateIn没什么顺序
            // TODO：初始化curUser
            curUser = userRepository.getFlowById(userId).stateIn(
                initialValue = User(),
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(0)
            )
            // TODO：初始化plan
            // 先查出来---TODO：这里要单独查一次，因为stateIn查询的时候，curUser还没有值！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
            val user = userRepository.getById(userId)
            // 再初始化plan
            plan = planRepository.getFlowByUserIdAndVocabulary(userId, user.vocabulary)
                .stateIn(
                    initialValue = Plan(),
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(0)
                )

            // TODO：初始化Today
            val today =
                todayRepository.getByUserIdAndYMD(
                    userId,
                    now.year,
                    now.monthValue,
                    now.dayOfMonth
                )
            if (today == null) {
                // 若不存在，则插入
                todayRepository.insert(Today(userId))
//                initLearnProcess()
            } else {
                // 若存在，则打开次数+1
                val selectedToday = todayRepository.getByUserIdAndYMD(
                    userId,
                    now.year,
                    now.monthValue,
                    now.dayOfMonth
                )
                selectedToday.openNum++
                todayRepository.update(selectedToday)
            }
        }
        // 很慢
        initVocabularySize()
    }

    private fun initVocabularySize() = viewModelScope.launch {
        val user = userRepository.getById(userId)
        if (user.vocabulary != "未选择") {
            _vocabularySize.value =
                vocabularyRepository.getVocabularyWords(
                    context,
                    Vocabulary.valueOf(user.vocabulary)
                ).size
        }
    }

    // 外部调用
    fun updateVocabulary(vocabulary: Vocabulary) = viewModelScope.launch {
        // 修改user中的vocabulary
        curUser.value.vocabulary = vocabulary.desc
        userRepository.update(curUser.value)
        // 修改当前的词库
//        _totalWords.value = vocabularyRepository.getVocabularyWords(context, vocabulary)
        // 修改Plan（有则赋值，没有则插入后赋值）
        updatePlan()
    }

    private fun updatePlan() = viewModelScope.launch {
        val selectedPlan = planRepository.getByUserIdAndVocabulary(userId, curUser.value.vocabulary)
        if (selectedPlan != null) {
            plan = planRepository.getFlowByUserIdAndVocabulary(userId, curUser.value.vocabulary)
                .stateIn(
                    initialValue = Plan(),
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000)
                )
        } else {
            // 先插进去
            planRepository.insert(Plan(userId, curUser.value.vocabulary))
            // 再查出来（这样LearnProcess和ReviewProcess就有值了————虽然也是空的）
            plan = planRepository.getFlowByUserIdAndVocabulary(userId, curUser.value.vocabulary)
                .stateIn(
                    initialValue = Plan(),
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000)
                )
        }
    }

    private fun initLearnProcess() = viewModelScope.launch {
//        val plan = planRepository.getByUserIdAndVocabulary(userId, curUser.value.vocabulary)
//        val learnProcess = getLearnProcess()
//        // 初始化dailyNum个词汇
//        for (i in 0..plan.value.dailyNum) {
//            learnProcess.process.add(PlanWord(i))
//        }
    }

    fun getLearnProcess(): LearnProcess {
        // TODO：要确保Plan不为Plan()
        Log.i("LearnReviewViewModel", "getLearnProcess: ${plan.value.learnProcess}")
        val learnProcessStr = plan.value.learnProcess
        return Gson().fromJson(learnProcessStr, LearnProcess::class.java)
    }

    fun getReviewProcess(): ReviewProcess {
        // TODO：要确保Plan不为Plan()
        val reviewProcessStr = plan.value.reviewProcess
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

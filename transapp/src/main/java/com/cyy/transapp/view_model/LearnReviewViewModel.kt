package com.cyy.transapp.view_model

import android.app.Activity
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cyy.app.word_bank.model.Word
import com.cyy.transapp.model.LearnDTO
import com.cyy.transapp.model.LearnProcess
import com.cyy.transapp.model.OpResult
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
import kotlin.concurrent.thread
import kotlin.math.min

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
    // TODO：这里需要外面再包一个MutableState，因为plan整体会被替换！！！
    var plan: MutableState<StateFlow<Plan>> = mutableStateOf(MutableStateFlow(Plan()))


    private val _vocabulary = MutableStateFlow(Word())
    val vocabulary: StateFlow<Word> = _vocabulary.asStateFlow()

    private val _loadVocabularyState = MutableStateFlow<OpResult<Any>>(OpResult.NotBegin)
    val loadVocabularyState: StateFlow<OpResult<Any>> = _loadVocabularyState.asStateFlow()

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
            // TODO：初始化curUser（Flow）
            curUser = userRepository.getFlowById(userId).stateIn(
                initialValue = User(),
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(0)
            )


            // TODO：初始化plan（Flow）
            val selectedUser = userRepository.getById(userId)
            // 再初始化plan
            plan.value =
                planRepository.getFlowByUserIdAndVocabulary(userId, selectedUser.vocabulary)
                    .stateIn(
                        initialValue = Plan(),
                        scope = viewModelScope,
                        started = SharingStarted.WhileSubscribed(0)
                    )


            // TODO：初始化Today（Flow）
            val selectedToday =
                todayRepository.getByUserIdAndYMD(
                    userId,
                    now.year,
                    now.monthValue,
                    now.dayOfMonth
                )
            if (selectedToday == null) {
                // ①若不存在，则插入
                todayRepository.insert(Today(userId))
                // 今天刚登录的话，需要初始化LearnProcess
                initLearnProcess()
            } else {
                // ②若存在，则打开次数+1
                selectedToday.openNum++
                todayRepository.update(selectedToday)
            }
        }


        // 加载字典
        loadVocabulary()
    }


    /**
     * 初始化LearnProcess（要把process的词数提升到dailyNum）
     * 场景1. 刚选完Vocabulary
     * 场景2. 今天刚登陆（若已经相等，则不变，即addNum=0）
     */
    private fun initLearnProcess() = viewModelScope.launch {
        val user = userRepository.getById(userId)
        if (user.vocabulary != "未选择") {
            val plan = planRepository.getByUserIdAndVocabulary(userId, user.vocabulary)!!
            val learnProcess = getLearnProcess(plan)
            // 初始化dailyNum个词汇
            val curNum = learnProcess.process.size
            val addNum = plan.dailyNum - curNum
            for (i in 0 until addNum) {
                learnProcess.process.add(PlanWord(learnProcess.learnedIdx + curNum))
            }
            learnProcess.learnedIdx += addNum
            updateLearnProcess(learnProcess)
        }
    }

    /**
     * 把更新后的LearnProcess保存到Plan中，并update(plan)！
     */
    private fun updateLearnProcess(learnProcess: LearnProcess) = viewModelScope.launch {
        val learnProcessStr = Gson().toJson(learnProcess)
        val user = userRepository.getById(userId)
        val plan = planRepository.getByUserIdAndVocabulary(userId, user.vocabulary)
        plan.learnProcess = learnProcessStr
        planRepository.update(plan)
    }

    /**
     * 根据传入的plan获得对应的LearnProcess
     */
    fun getLearnProcess(plan: Plan): LearnProcess {
        val learnProcessStr = plan.learnProcess
        return Gson().fromJson(learnProcessStr, LearnProcess::class.java)
    }

    /**
     * 根据传入的plan获得对应的ReviewProcess
     */
    fun getReviewProcess(plan: Plan): ReviewProcess {
        val reviewProcessStr = plan.reviewProcess
        return Gson().fromJson(reviewProcessStr, ReviewProcess::class.java)
    }

    /**
     * 根据user中的vocabulary加载字典
     */
    private fun loadVocabulary() {
        _loadVocabularyState.value = OpResult.Loading
        viewModelScope.launch {
            val user = userRepository.getById(userId)
            thread {
                if (user.vocabulary != "未选择") {
                    _vocabulary.value =
                        vocabularyRepository.getVocabularyWord(
                            context,
                            Vocabulary.valueOf(user.vocabulary)
                        )
                }
                _loadVocabularyState.value = OpResult.Success("加载完成")
            }
        }
    }
    // --------------------------------------------更换字典 part--------------------------------------------

    /**
     * 更换Vocabulary后，更新user中的Vocabulary，更新要展示的plan（可能不存在，则要创建）
     */
    // 外部调用（选择字典的时候）
    fun updateVocabulary(vocabulary: Vocabulary) = viewModelScope.launch {
        // 修改user中的vocabulary
        curUser.value.vocabulary = vocabulary.desc
        userRepository.update(curUser.value)
        // 修改Plan（有则赋值，没有则插入后赋值）
        updatePlan()
        // 更新
        loadVocabulary()
    }

    /**
     * 在updateVocabulary()内部调用，更新Plan（有则赋值，没有则插入后赋值）
     */
    private fun updatePlan() = viewModelScope.launch {
        val selectedPlan = planRepository.getByUserIdAndVocabulary(userId, curUser.value.vocabulary)
        if (selectedPlan != null) {
            plan.value =
                planRepository.getFlowByUserIdAndVocabulary(userId, curUser.value.vocabulary)
                    .stateIn(
                        initialValue = Plan(),
                        scope = viewModelScope,
                        started = SharingStarted.WhileSubscribed(5000)
                    )
        } else {
            // 先插进去
            planRepository.insert(Plan(userId, curUser.value.vocabulary))
            // 再查出来（这样LearnProcess和ReviewProcess就有值了————虽然也是空的）
            plan.value =
                planRepository.getFlowByUserIdAndVocabulary(userId, curUser.value.vocabulary)
                    .stateIn(
                        initialValue = Plan(),
                        scope = viewModelScope,
                        started = SharingStarted.WhileSubscribed(5000)
                    )
            // 刚创建完Plan后，需要初始化LearnProcess
            initLearnProcess()
        }
    }


    fun getLearnDTO(): LearnDTO {
        val learnProcess = getLearnProcess(plan.value.value)
        val gap = plan.value.value.dailyNum * 2
        val startIdx = Integer.max(
            learnProcess.process[0].index - gap,
            0
        )
        val endIdx = min(
            learnProcess.learnedIdx + gap,
            vocabulary.value.size
        )
//        val allWordsStr = Gson().toJson(vocabulary.value.subList(startIdx, endIdx).toList())
        val allWordsStr = Gson().toJson(vocabulary.value)
        Log.i("Lookkkkkkkkkkkkk", allWordsStr)
        return LearnDTO(
            userId,
            curUser.value.vocabulary,
            allWordsStr,
            startIdx,
            endIdx
        )
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

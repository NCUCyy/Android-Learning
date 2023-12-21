package com.cyy.transapp.view_model.learn_review

import android.app.Activity
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cyy.app.word_bank.model.Word
import com.cyy.app.word_bank.model.WordItem
import com.cyy.transapp.model.OpResult
import com.cyy.transapp.model.PlanWord
import com.cyy.transapp.model.QuizType
import com.cyy.transapp.model.QuizWord
import com.cyy.transapp.model.ReviewProcess
import com.cyy.transapp.model.Vocabulary
import com.cyy.transapp.pojo.Plan
import com.cyy.transapp.pojo.StarWord
import com.cyy.transapp.pojo.User
import com.cyy.transapp.repository.PlanRepository
import com.cyy.transapp.repository.StarWordRepository
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
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit
import kotlin.concurrent.thread

class ReviewViewModel(
    val userId: Int,
    private val vocabulary: String,
    private val context: Activity,
    private val userRepository: UserRepository,
    private val todayRepository: TodayRepository,
    private val planRepository: PlanRepository,
    private val transRepository: TransRepository,
    private val vocabularyRepository: VocabularyRepository,
    private val starWordRepository: StarWordRepository
) : ViewModel() {
    // 开始时间
    private val startTime = OffsetDateTime.now()
    private val now = OffsetDateTime.now()

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


    // TODO：当前选择的单词的索引（在ReviewProcess的process列表中的索引）
    private val _curIdx = MutableStateFlow(0)
    val curIdx = _curIdx.asStateFlow()

    // TODO：当前选择的————PlanWord
    private val _curPlanWord = MutableStateFlow(PlanWord())
    val curPlanWord = _curPlanWord.asStateFlow()

    // TODO：当前的题目————QuizWord
    private val _curQuizWord = MutableStateFlow(QuizWord())
    val curQuizWord = _curQuizWord.asStateFlow()

    // 当前的WordItem
    private val _curWordItem = MutableStateFlow(WordItem())
    val curWordItem = _curWordItem.asStateFlow()


    // TODO：当前的单词的process
    private val _curWordProcess = MutableStateFlow(0)
    val curWordProcess = _curWordProcess.asStateFlow()


    // TODO：字典的加载状态
    private val _loadVocabularyState = MutableStateFlow<OpResult<Any>>(OpResult.NotBegin)
    val loadVocabularyState: StateFlow<OpResult<Any>> = _loadVocabularyState.asStateFlow()

    // TODO：字典中的所有【WordItem】
    private lateinit var allWords: Word

    // TODO：关键！------用于判断当前的词是否已经收藏
    //  （注意是MutableState<StateFLow>的结构：StateFlow用于观察数据库中的变化，MutableState用于nextWord后更换curWord）
    val starWord = mutableStateOf(
        starWordRepository.getFlowStarWordByUserIdAndWord(userId, _curQuizWord.value.word).stateIn(
            initialValue = null,
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(0)
        )
    )

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
        val reviewProcess = Gson().fromJson(plan.reviewProcess, ReviewProcess::class.java)
        // 构造出一个
        if (reviewProcess.process.size > 0) {
            // TODO：1、若还有词，则构造一个QuizWord
            // 选中最佳的索引(在ReviewProcess的process列表中的索引)
            _curIdx.value = getNextIdx(reviewProcess.process)
            // 更新所有的interval
            updateInterval(plan, reviewProcess)
            // 配置下一个词
            configNext(reviewProcess)
        } else {
            // TODO：2、若没有词了，则结束学习！
            _loadVocabularyState.value = OpResult.NotBegin
        }
    }

    /**
     * 1、curPlanWord
     * 2、curQuizWord
     * 3、curOption
     * 4、curWordProcess
     * 5、isCurStared
     */
    private fun configNext(reviewProcess: ReviewProcess) {
        _curPlanWord.value = reviewProcess.process[_curIdx.value]
        // 取出在总字典中的索引，根据这个idx，构造出一个QuizWord（随机）
        _curQuizWord.value = QuizWord(_curPlanWord.value.index, allWords, QuizType.Review)
        // 当前PlanWord的process
        _curWordProcess.value = _curPlanWord.value.process
        // 当前单词的WordItem
        _curWordItem.value = allWords[_curPlanWord.value.index]
        // 更新这个单词是否收藏
        starWord.value =
            starWordRepository.getFlowStarWordByUserIdAndWord(userId, _curQuizWord.value.word)
                .stateIn(
                    initialValue = StarWord(),
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(0)
                )
    }

    /**
     * 更新所有ReviewProcess中PlanWord的interval
     */
    private fun updateInterval(plan: Plan, reviewProcess: ReviewProcess) {
        for (i in 0 until reviewProcess.process.size) {
            if (i == _curIdx.value) {
                reviewProcess.process[i].interval = 0
            } else {
                reviewProcess.process[i].interval++
            }
        }
        updatePlanByReviewProcess(plan, reviewProcess)
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
     * 选择认识
     */
    fun setKnown() = viewModelScope.launch {
        // process直接给3（直接完成）
        // TODO：及时更新wordProcess用于UI显示
        _curWordProcess.value = 3
        val plan = planRepository.getByUserIdAndVocabulary(userId, vocabulary)
        val reviewProcess = getReviewProcess(plan)
        // 更新ReviewProcess（process）---直接删掉这个词
        reviewProcess.process.removeAt(_curIdx.value)
        updatePlanByReviewProcess(plan, reviewProcess)
        // 更新Today（reviewNum）
        updateTodayByNewReview()

    }

    /**
     * 选择模糊
     */
    fun setAmbitious() = viewModelScope.launch {
        // process+1
        // TODO：及时更新wordProcess用于UI显示
        _curWordProcess.value++
        val plan = planRepository.getByUserIdAndVocabulary(userId, vocabulary)
        val reviewProcess = getReviewProcess(plan)
        reviewProcess.process[_curIdx.value].process += 1
        // 更新ReviewProcess（process）
        updatePlanByReviewProcess(plan, reviewProcess)

    }

    /**
     * 选择不认识
     */
    fun setUnknown() = viewModelScope.launch {
        // process清0
        // TODO：及时更新wordProcess用于UI显示
        _curWordProcess.value = 0
        val plan = planRepository.getByUserIdAndVocabulary(userId, vocabulary)
        val reviewProcess = getReviewProcess(plan)
        reviewProcess.process[_curIdx.value].process = 0
        // 更新ReviewProcess（process）
        updatePlanByReviewProcess(plan, reviewProcess)

    }


    /**
     * 根据Plan获得ReviewProcess
     */
    private fun getReviewProcess(plan: Plan): ReviewProcess {
        return Gson().fromJson(plan.reviewProcess, ReviewProcess::class.java)!!
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
     * 取消收藏
     */
    fun unstarWord() = viewModelScope.launch {
        starWordRepository.getStarWordByUserIdAndWord(userId, _curQuizWord.value.word)?.let {
            starWordRepository.delete(it)
        }
    }

    /**
     * 收藏
     */
    fun starWord() = viewModelScope.launch {
        starWordRepository.insert(
            StarWord(userId, _curQuizWord.value.word)
        )
        updateTodayByStar()
    }

    /**
     * 移除单词
     */
    fun removeWord() = viewModelScope.launch {
        // 1、更新Plan
        updatePlanByRemove()
        // 2、更新Today
        updateTodayByRemove()
        nextWord()
    }

    /**
     * 复习完了一个词，today中的reviewNum要+1
     */
    private suspend fun updateTodayByNewReview() {
        val today =
            todayRepository.getByUserIdAndYMD(userId, now.year, now.monthValue, now.dayOfMonth)
        today.reviewNum++
        todayRepository.update(today)
    }

    private suspend fun updatePlanByRemove() {
        val plan = planRepository.getByUserIdAndVocabulary(userId, vocabulary)
        val reviewProcess = getReviewProcess(plan)
        // 直接从ReviewProcess中移除即可！
        reviewProcess.process.removeAt(_curIdx.value)
        // 更新数据库中的plan
        updatePlanByReviewProcess(plan, reviewProcess)
    }

    private suspend fun updateTodayByRemove() {
        val today =
            todayRepository.getByUserIdAndYMD(userId, now.year, now.monthValue, now.dayOfMonth)
        today.removeNum++
        todayRepository.update(today)
    }

    private suspend fun updateTodayByStar() {
        val today =
            todayRepository.getByUserIdAndYMD(userId, now.year, now.monthValue, now.dayOfMonth)
        today.starNum++
        todayRepository.update(today)
    }

    /**
     * 结束学习！（更新时间）
     */
    fun endReview() = viewModelScope.launch {
        val endTime = OffsetDateTime.now()
        // 更新Today
        updateTodayByTime(endTime)
    }

    private suspend fun updateTodayByTime(endTime: OffsetDateTime) {
        // 计算两个时间的差值
        val duration = ChronoUnit.MINUTES.between(startTime, endTime)
        // 更新Today
        val today =
            todayRepository.getByUserIdAndYMD(userId, now.year, now.monthValue, now.dayOfMonth)
        today.learnTime += duration.toInt()
        todayRepository.update(today)
    }
}

class ReviewViewModelFactory(
    private val userId: Int,
    private val vocabulary: String,
    private val context: Activity,
    private val userRepository: UserRepository,
    private val todayRepository: TodayRepository,
    private val planRepository: PlanRepository,
    private val transRepository: TransRepository,
    private val vocabularyRepository: VocabularyRepository,
    private val starWordRepository: StarWordRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReviewViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReviewViewModel(
                userId,
                vocabulary,
                context,
                userRepository,
                todayRepository,
                planRepository,
                transRepository,
                vocabularyRepository,
                starWordRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

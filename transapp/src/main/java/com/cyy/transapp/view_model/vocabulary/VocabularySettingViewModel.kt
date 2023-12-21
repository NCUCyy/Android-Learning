package com.cyy.transapp.view_model.vocabulary

import android.app.Activity
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cyy.app.word_bank.model.Word
import com.cyy.transapp.model.LearnProcess
import com.cyy.transapp.model.OpResult
import com.cyy.transapp.model.PlanWord
import com.cyy.transapp.model.Vocabulary
import com.cyy.transapp.pojo.Plan
import com.cyy.transapp.pojo.User
import com.cyy.transapp.repository.PlanRepository
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

class VocabularySettingViewModel(
    private val userId: Int,
    private val vocabulary: String,
    private val context: Activity,
    private val userRepository: UserRepository,
    private val planRepository: PlanRepository,
    private val vocabularyRepository: VocabularyRepository
) : ViewModel() {
    private val curUser = userRepository.getFlowById(userId).stateIn(
        initialValue = User(),
        started = SharingStarted.WhileSubscribed(5000),
        scope = viewModelScope
    )
    val curPlanState = mutableStateOf(
        planRepository.getFlowByUserIdAndVocabulary(userId, vocabulary).stateIn(
            initialValue = Plan(),
            started = SharingStarted.WhileSubscribed(5000),
            scope = viewModelScope
        )
    )

    private val _loadVocabularyState = MutableStateFlow<OpResult<Any>>(OpResult.NotBegin)
    val loadVocabularyState: StateFlow<OpResult<Any>> = _loadVocabularyState.asStateFlow()

    private val _curVocabulary = MutableStateFlow(Word())
    val curVocabulary: StateFlow<Word> = _curVocabulary.asStateFlow()

    fun getAllVocabulary(): List<Vocabulary> = vocabularyRepository.vocabularies


    /**
     * 更换Vocabulary后，更新user中的Vocabulary，更新要展示的plan（可能不存在，则要创建）
     */
    // 外部调用（选择字典的时候）
    fun updateVocabulary(vocabulary: Vocabulary) = viewModelScope.launch {
        // 修改user中的vocabulary
        curUser.value.vocabulary = vocabulary.desc
        userRepository.update(curUser.value)
        updatePlan()
        // TODO：可以去掉
        loadVocabulary()
    }

    /**
     * 在updateVocabulary()内部调用，更新Plan（有则赋值，没有则插入后赋值）
     */
    private suspend fun updatePlan() {
        val selectedPlan = planRepository.getByUserIdAndVocabulary(userId, curUser.value.vocabulary)
        if (selectedPlan != null) {
            curPlanState.value =
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
            curPlanState.value =
                planRepository.getFlowByUserIdAndVocabulary(userId, curUser.value.vocabulary)
                    .stateIn(
                        initialValue = Plan(),
                        scope = viewModelScope,
                        started = SharingStarted.WhileSubscribed(5000)
                    )
            // 刚创建完Plan后，需要初始化LearnProcess
        }
        initLearnProcess()
    }

    private fun loadVocabulary() {
        _loadVocabularyState.value = OpResult.Loading
        viewModelScope.launch {
            val user = userRepository.getById(userId)
            thread {
                if (user.vocabulary != "未选择") {
                    _curVocabulary.value =
                        vocabularyRepository.getVocabularyWord(
                            context,
                            Vocabulary.valueOf(user.vocabulary)
                        )
                }
                _loadVocabularyState.value = OpResult.Success("加载完成")
            }
        }
    }

    private suspend fun initLearnProcess() {
        val user = userRepository.getById(userId)
        if (user.vocabulary != "未选择") {
            val plan = planRepository.getByUserIdAndVocabulary(userId, user.vocabulary)
            val learnProcess = getLearnProcess(plan)
            // 初始化dailyNum个词汇
            val curNum = learnProcess.process.size
            val addNum = plan.dailyNum - curNum
            for (i in 0 until addNum) {
                learnProcess.process.add(PlanWord(learnProcess.learnedIdx + i))
            }
            learnProcess.learnedIdx += addNum
            updateLearnProcess(learnProcess)
            Log.i("VocabularySettingViewModel", "initLearnProcess: ${learnProcess.process}")
        }
    }

    private suspend fun updateLearnProcess(learnProcess: LearnProcess) {
        val learnProcessStr = Gson().toJson(learnProcess)
        val user = userRepository.getById(userId)
        val plan = planRepository.getByUserIdAndVocabulary(userId, user.vocabulary)
        plan.learnProcess = learnProcessStr
        planRepository.update(plan)
    }

    private fun getLearnProcess(plan: Plan): LearnProcess {
        val learnProcessStr = plan.learnProcess
        return Gson().fromJson(learnProcessStr, LearnProcess::class.java)
    }

}

class VocabularySettingViewModelFactory(
    private val userId: Int,
    private val vocabulary: String,
    private val context: Activity,
    private val userRepository: UserRepository,
    private val planRepository: PlanRepository,
    private val vocabularyRepository: VocabularyRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VocabularySettingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VocabularySettingViewModel(
                userId,
                vocabulary,
                context,
                userRepository,
                planRepository,
                vocabularyRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

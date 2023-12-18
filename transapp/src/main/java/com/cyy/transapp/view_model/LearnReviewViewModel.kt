package com.cyy.transapp.view_model

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
    private val userRepository: UserRepository,
    private val todayRepository: TodayRepository,
    private val planRepository: PlanRepository
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

    init {
        // TODO：②初始化today
        viewModelScope.launch {
            val today =
                todayRepository.getByUserIdAndYMD(userId, now.year, now.monthValue, now.dayOfMonth)
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
        // TODO：③初始化plan
        viewModelScope.launch {
//            updatePlan()
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
        curUser.value.vocabulary = vocabulary.desc
        userRepository.update(curUser.value)
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
        }
    }

    private fun getLearnProcessFromPlan(): LearnProcess {
        // TODO：要确保Plan不为Plan()
        val learnProcessStr = _plan.value.learnProcess
        return Gson().fromJson(learnProcessStr, LearnProcess::class.java)
    }

    private fun getReviewProcessFromPlan(): ReviewProcess {
        // TODO：要确保Plan不为Plan()
        val reviewProcessStr = _plan.value.reviewProcess
        return Gson().fromJson(reviewProcessStr, ReviewProcess::class.java)
    }
}

class LearnReviewViewModelFactory(
    private val userId: Int,
    private val userRepository: UserRepository,
    private val todayRepository: TodayRepository,
    private val planRepository: PlanRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LearnReviewViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LearnReviewViewModel(
                userId, userRepository, todayRepository, planRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

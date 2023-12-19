package com.cyy.transapp.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cyy.transapp.repository.PlanRepository
import com.cyy.transapp.repository.TodayRepository
import com.cyy.transapp.repository.TransRepository
import com.cyy.transapp.repository.UserRepository

class LearnViewModel(
    private val userId: Int,
    private val vocabulary: String,
    private val userRepository: UserRepository,
    private val todayRepository: TodayRepository,
    private val planRepository: PlanRepository,
    private val transRepository: TransRepository
) : ViewModel() {
}

class LearnViewModelFactory(
    private val userId: Int,
    private val vocabulary: String,
    private val userRepository: UserRepository,
    private val todayRepository: TodayRepository,
    private val planRepository: PlanRepository,
    private val transRepository: TransRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LearnViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LearnViewModel(
                userId,
                vocabulary,
                userRepository,
                todayRepository,
                planRepository,
                transRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

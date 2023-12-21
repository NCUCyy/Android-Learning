package com.cyy.transapp.activity.main.view

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.cyy.transapp.TransApp
import com.cyy.transapp.activity.main.Screen
import com.cyy.transapp.activity.main.StateHolder
import com.cyy.transapp.model.Vocabulary
import com.cyy.transapp.view_model.LearnReviewViewModel
import com.cyy.transapp.view_model.LearnReviewViewModelFactory
import com.cyy.transapp.view_model.QueryViewModel
import com.cyy.transapp.view_model.QueryViewModelFactory

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun NavigationGraphScreen(states: StateHolder, userId: Int, vocabulary: Vocabulary) {
    val application = LocalContext.current.applicationContext as TransApp
    val context = LocalContext.current as Activity
    val queryViewModel = viewModel<QueryViewModel>(
        factory = QueryViewModelFactory(
            userId,
            application.transRepository,
            application.sentenceRepository
        )
    )

    val learnReviewViewModel = viewModel<LearnReviewViewModel>(
        factory = LearnReviewViewModelFactory(
            userId,
            context,
            application.userRepository,
            application.todayRepository,
            application.planRepository,
            application.vocabularyRepository
        )
    )

    if (vocabulary != Vocabulary.NOT_SELECTED) {
        // 选择Vocabulary后执行（仅一次）
        learnReviewViewModel.updateVocabulary(vocabulary)
    }

    // 定义宿主(需要：导航控制器、导航起点---String类型)
    NavHost(navController = states.navController, startDestination = states.startDestination) {
        // 根据route进行页面的匹配
        // 页面1：翻译
        composable(route = Screen.QueryPage.route) {
            // 1、更新当前显示的Screen
            states.currentScreen.value = Screen.QueryPage
            // 2、此语句处才会展示指定的Screen
            QueryScreen(states, queryViewModel)
        }
        // 页面2：听力
        composable(route = Screen.ListenPage.route) {
            // 1、更新当前显示的Screen
            states.currentScreen.value = Screen.ListenPage
            // 2、此语句处才会展示指定的Screen
            ListenScreen(states)
        }
        // 页面3
        composable(route = Screen.LearnPage.route) {
            // 1、更新当前显示的Screen
            states.currentScreen.value = Screen.LearnPage
            // 2、此语句处才会展示指定的Screen
            LearnScreen(states, learnReviewViewModel)
        }
    }
}
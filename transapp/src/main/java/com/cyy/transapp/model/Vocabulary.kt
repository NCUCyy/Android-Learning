package com.cyy.transapp.model

import com.cyy.transapp.R


enum class Vocabulary(val desc: String, val icon: Int, val fileDir: Int) {
    NOT_SELECTED("未选择", 0, 0),
    CET4("CET-4", R.drawable.correct, R.raw.cet4),
    CET6("CET-6", R.drawable.correct, R.raw.cet6),
    TOEFL("TOEFL", R.drawable.correct, R.raw.toefl),
    SAT("SAT", R.drawable.correct, R.raw.sat),
    KAOYAN("考研", R.drawable.correct, R.raw.kaoyan),
    GAOZHONG("高中", R.drawable.correct, R.raw.gaozhong),
    CHUZHONG("初中", R.drawable.correct, R.raw.chuzhong);
}
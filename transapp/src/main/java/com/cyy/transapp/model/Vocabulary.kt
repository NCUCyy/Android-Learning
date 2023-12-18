package com.cyy.transapp.model

import com.cyy.transapp.R


enum class Vocabulary(val desc: String, val icon: Int, val fileDir: Int) {
    NOT_SELECTED("未选择", 0, 0),
    CET4("CET4", R.drawable.correct, R.raw.cet4),
    CET6("CET6", R.drawable.correct, R.raw.cet6),
    TOEFL("TOEFL", R.drawable.correct, R.raw.toefl),
    SAT("SAT", R.drawable.correct, R.raw.sat)
}

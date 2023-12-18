package com.cyy.transapp.model

import com.cyy.transapp.R

var baseDir =
    "/Users/cyy/AndroidStudioProjects/Chenyi/transapp/src/main/java/com/cyy/transapp/repository/vocabulary/"

enum class Vocabulary(val desc: String, val icon: Int, val fileDir: String) {
    NOT_SELECTED("", 0, ""),
    CET4("CET4", R.drawable.correct, baseDir + "CET4.json"),
    CET6("CET6", R.drawable.correct, baseDir + "CET6.json"),
    TOEFL("TOEFL", R.drawable.correct, baseDir + "TOEFL.json"),
}

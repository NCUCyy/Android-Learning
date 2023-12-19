package com.cyy.transapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * 注意：这个类是用于传递数据的，不是数据库中的表
 * 另外：主构造函数中必须包含所有的属性，否则传过去是空的！
 */
@Parcelize
data class LearnDTO(
    var userId: Int,
    var vocabulary: String,
    var allWordsStr: String,
    var starIdx: Int,
    var endIdx: Int
) : Parcelable

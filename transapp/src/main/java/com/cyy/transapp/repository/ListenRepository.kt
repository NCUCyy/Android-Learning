package com.cyy.transapp.repository

import com.cyy.transapp.R
import com.cyy.transapp.pojo.ListenResource

class ListenRepository {
    val listenResources = listOf(
        ListenResource(
            "Hobbies and Studies",
            R.raw.hobbies_and_studies_img,
            R.raw.hobbies_and_studies_mp3,
            R.raw.hobbies_and_studies_en,
            R.raw.hobbies_and_studies_zh
        ),
    )
}
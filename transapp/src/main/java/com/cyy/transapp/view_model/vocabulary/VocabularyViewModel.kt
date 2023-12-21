package com.cyy.transapp.view_model.vocabulary

import com.cyy.transapp.repository.VocabularyRepository

class VocabularyViewModel(private val vocabularyRepository: VocabularyRepository) {
    val vocabularies = vocabularyRepository.vocabularies
}
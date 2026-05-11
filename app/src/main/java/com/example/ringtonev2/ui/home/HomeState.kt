package com.example.ringtonev2.ui.home

import com.example.ringtonev2.domain.Category
import com.example.ringtonev2.domain.Ringtone


sealed interface HomeState {

    data object Idle : HomeState

    data object Loading : HomeState

    data class Success(
        val categories: List<Category>,
        val selectedCategoryId: Int?
    ) : HomeState

    data class Error(
        val message: String
    ) : HomeState
}
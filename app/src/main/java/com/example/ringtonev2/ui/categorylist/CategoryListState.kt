package com.example.ringtonev2.ui.categorylist

import com.example.ringtonev2.domain.Category

sealed class CategoryState {
    data object Idle : CategoryState()
    data object Loading : CategoryState()
    data class Success(
        val categories: List<Category>,
        val selectedCategoryId: Int?,
        val categoryName: String?
    ) : CategoryState()
    data class Error(val message: String) : CategoryState()
}
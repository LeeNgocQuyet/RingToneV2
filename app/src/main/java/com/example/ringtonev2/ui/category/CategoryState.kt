package com.example.ringtonev2.ui.category

import com.example.ringtonev2.domain.Category

sealed class CategoryState {
    object Idle : CategoryState()
    object Loading : CategoryState()
    data class Success(val list: List<Category>) : CategoryState()
    data class Error(val message: String) : CategoryState()
}
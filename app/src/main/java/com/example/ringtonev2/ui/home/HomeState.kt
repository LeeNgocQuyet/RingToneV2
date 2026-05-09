package com.example.ringtonev2.ui.home

import com.example.ringtonev2.data.remote.dto.TikTokData
import com.example.ringtonev2.domain.Category
import com.example.ringtonev2.domain.Ringtone

data class HomeUiState(
    val isLoading: Boolean = false,
    val ringtones: List<Ringtone> = emptyList(),
    val categories: List<Category> = emptyList(),
    val selectedCategoryId: Int? = null,
    val error: String? = null
)
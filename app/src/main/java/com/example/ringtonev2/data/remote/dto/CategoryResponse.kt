package com.example.ringtonev2.data.remote.dto

import com.example.ringtonev2.domain.Category

data class CategoryResponse(
    val status: Boolean,
    val data: List<Category>,
    val total: Int
)
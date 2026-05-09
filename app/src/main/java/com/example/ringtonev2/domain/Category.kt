package com.example.ringtonev2.domain

import com.google.gson.annotations.SerializedName

data class Category(
    val id: Int,
    val icon: String,
    val name: String,
    val weight: Int,
    @SerializedName("background_image")
    val backgroundImage: String,
    val description: String
)
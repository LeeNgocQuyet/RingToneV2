package com.example.ringtonev2.data.remote.dto

import com.example.ringtonev2.domain.Category
import com.google.gson.annotations.SerializedName

/*
"status": true,
    "data": [
        {
            "id": 1,
            "icon": "https://cdn-app.arsfan.site//release_24/5b500820b037a69a05008b144422d091.png",
            "name": "Alarm",
            "weight": 1,
            "background_image": "https://cdn-app.arsfan.site//release_24/01c0140299b93aabc670dc0f9391df20.jpg",
            "description": "this alarm ringtone"
        },
 */
data class CategoryResponse(

    val status: Boolean,
    val data: List<Category>,
    val total: Int
)
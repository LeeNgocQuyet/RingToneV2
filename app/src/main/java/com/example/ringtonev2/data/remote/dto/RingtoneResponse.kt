package com.example.ringtonev2.data.remote.dto

import com.example.ringtonev2.domain.Ringtone


data class RingtoneResponse(
    val status: Boolean,
    val data: List<Ringtone>,
    val total: Int,
    val page: Int,
    val limit: Int
)


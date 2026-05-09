package com.example.ringtonev2.data.mapper

import com.example.ringtonev2.data.local.entity.DownloadedRingtone
import com.example.ringtonev2.domain.DownloadItem

fun DownloadedRingtone.toDomain(): DownloadItem = DownloadItem(
    id = id,
    ringtoneId = ringtoneId,
    title = title,
    artist = artist,
    filePath = filePath,
    downloadedAt = downloadedAt,
    duration = duration
)
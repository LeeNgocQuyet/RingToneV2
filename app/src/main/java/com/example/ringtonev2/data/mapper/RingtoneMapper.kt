package com.example.ringtonev2.data.mapper

import com.example.ringtonev2.data.local.entity.DownloadEntity
import com.example.ringtonev2.domain.DownloadItem
import com.example.ringtonev2.domain.Ringtone

fun DownloadEntity.toDomain(): DownloadItem = DownloadItem(
    id = id,
    ringtoneId = ringtoneId,
    title = title,
    artist = artist,
    filePath = filePath,
    downloadedAt = downloadedAt,
)
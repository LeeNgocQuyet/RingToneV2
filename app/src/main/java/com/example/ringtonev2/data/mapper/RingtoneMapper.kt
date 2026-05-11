package com.example.ringtonev2.data.mapper

import com.example.ringtonev2.data.local.entity.DownloadedRingtone
import com.example.ringtonev2.data.local.entity.FavoriteEntity
import com.example.ringtonev2.domain.DownloadItem
import com.example.ringtonev2.domain.Ringtone

fun DownloadedRingtone.toDomain(): DownloadItem = DownloadItem(
    id = id,
    ringtoneId = ringtoneId,
    title = title,
    artist = artist,
    filePath = filePath,
    downloadedAt = downloadedAt,
    duration = duration
)
fun Ringtone.toFavoriteEntity(): FavoriteEntity {
    return FavoriteEntity(
        ringtoneId = id.toString(),
        title = name ?: "",
        categoryId = categoryId ?: 0,
        duration = duration ?: 0,
        audioUrl = audioPath ?: "",
    )
}
fun FavoriteEntity.toDomain(): Ringtone {
    return Ringtone(
        id = ringtoneId.toInt(),
        name = title,
        duration = duration,
        audioPath = audioUrl,
        categoryId = categoryId,
        image = null,
        watchCount = null
    )
}
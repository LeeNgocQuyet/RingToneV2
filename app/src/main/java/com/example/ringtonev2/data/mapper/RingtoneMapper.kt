package com.example.ringtonev2.data.mapper

import android.net.Uri
import com.example.ringtonev2.data.local.entity.TikTokDownloadEntity
import com.example.ringtonev2.data.local.entity.FavoriteEntity
import com.example.ringtonev2.data.local.entity.DownloadedRingtoneEntity
import com.example.ringtonev2.domain.DownloadItem
import com.example.ringtonev2.domain.Ringtone
import com.example.ringtonev2.domain.RingtoneAudioPreview
import java.io.File

fun TikTokDownloadEntity.toDomain(): DownloadItem = DownloadItem(
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
        ringtoneId = id,
        title = name ?: "",
        categoryId = categoryId ?: 0,
        duration = duration ?: 0,
        audioUrl = audioPath ?: "",
    )
}
fun FavoriteEntity.toDomain(): Ringtone {
    return Ringtone(
        id = ringtoneId,
        name = title,
        duration = duration,
        audioPath = audioUrl,
        categoryId = categoryId,
        image = null,
        watchCount = null
    )
}
fun DownloadedRingtoneEntity.toDomain(): Ringtone {
    return Ringtone(
        id = id,
        name = title,
        duration = duration,
        audioPath = audioUrl,
        categoryId = null,
        image = coverUrl,
        watchCount = plays
    )
}

fun Ringtone.toDownloadedRingtoneEntity(
    position: Int = 0,
    filePath: String): DownloadedRingtoneEntity {
    return DownloadedRingtoneEntity(
        id = id,
        position = position,
        title = name ?: "",
        artist = "",
        category = "",
        duration = duration ?: 0,
        coverUrl = image ?: "",
        audioUrl = audioPath ?: "",
        plays = watchCount ?: 0,
        cachedAt = System.currentTimeMillis(),
        filePath = filePath
    )
}

fun Ringtone.toAudioPreview(
    isDownloaded: Boolean = false,
    audioPathOverride: String? = null
): RingtoneAudioPreview {
    return RingtoneAudioPreview(
        ringtoneId = id,
        title = name ?: "",
        audioPath = audioPathOverride ?: audioPath.orEmpty(),
        duration = duration,
        isDownloaded = isDownloaded
    )
}
fun DownloadItem.toRingtone(): Ringtone {
    return Ringtone(
        id = ringtoneId,
        name = title,
        duration = duration,
        audioPath = filePath,
        categoryId = null,
        image = null,
        watchCount = null
    )
}

fun TikTokDownloadEntity.toDownloadedRingtoneEntity(): DownloadedRingtoneEntity {
    return DownloadedRingtoneEntity(
        id = ringtoneId,
        title = title,
        artist = artist,
        category = "",
        duration = duration*1000,
        coverUrl = "",
        audioUrl = Uri.fromFile(File(filePath)).toString(),
        plays = 0,
        cachedAt = downloadedAt,
        filePath = filePath,
        position = 0
    )
}

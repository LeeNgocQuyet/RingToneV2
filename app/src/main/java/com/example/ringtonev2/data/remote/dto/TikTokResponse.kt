package com.example.ringtonev2.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Response wrapper trả về từ API:
 *   GET https://api-project2.h5cdn.com/api/ringtone/tiktok?url=<link>
 *
 * JSON mẫu:
 * {
 *   "status": true,
 *   "data": {
 *     "code": 0,
 *     "msg": "success",
 *     "processed_time": 0.29,
 *     "data": {
 *       "id": "...",
 *       "title": "...",
 *       "music": "https://.../audio.mp3",
 *       "play": "...",
 *       "music_info": { ... },
 *       "author": { ... },
 *       ...
 *     }
 *   }
 * }
 *
 * URL audio = response.data.data.music
 */
data class TikTokResponse(
    @SerializedName("status") val status: Boolean? = null,
    @SerializedName("data") val data: TikTokWrapper? = null
)

data class TikTokWrapper(
    @SerializedName("code") val code: Int? = null,
    @SerializedName("msg") val msg: String? = null,
    @SerializedName("processed_time") val processedTime: Double? = null,
    @SerializedName("data") val data: TikTokData? = null
)

data class TikTokData(
    @SerializedName("aweme_id") val awemeId: String? = null,
    @SerializedName("id") val id: String? = null,
    @SerializedName("region") val region: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("cover") val cover: String? = null,
    @SerializedName("origin_cover") val originCover: String? = null,
    @SerializedName("ai_dynamic_cover") val aiDynamicCover: String? = null,
    @SerializedName("duration") val duration: Long? = null,
    @SerializedName("play") val play: String? = null,
    @SerializedName("wmplay") val wmPlay: String? = null,
    @SerializedName("hdplay") val hdPlay: String? = null,
    @SerializedName("size") val size: Long? = null,
    @SerializedName("wm_size") val wmSize: Long? = null,
    @SerializedName("hd_size") val hdSize: Long? = null,
    @SerializedName("music") val music: String? = null,
    @SerializedName("music_info") val musicInfo: MusicInfo? = null,
    @SerializedName("author") val author: Author? = null,
    @SerializedName("play_count") val playCount: Long? = null,
    @SerializedName("digg_count") val diggCount: Long? = null,
    @SerializedName("comment_count") val commentCount: Long? = null,
    @SerializedName("share_count") val shareCount: Long? = null,
    @SerializedName("download_count") val downloadCount: Long? = null,
    @SerializedName("collect_count") val collectCount: Long? = null,
    @SerializedName("create_time") val createTime: Long? = null
)

data class MusicInfo(
    @SerializedName("id") val id: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("play") val play: String? = null,
    @SerializedName("cover") val cover: String? = null,
    @SerializedName("author") val author: String? = null,
    @SerializedName("original") val original: Boolean? = null,
    @SerializedName("duration") val duration: Long? = null,
    @SerializedName("album") val album: String? = null
)

data class Author(
    @SerializedName("id") val id: String? = null,
    @SerializedName("unique_id") val uniqueId: String? = null,
    @SerializedName("nickname") val nickname: String? = null,
    @SerializedName("avatar") val avatar: String? = null
)

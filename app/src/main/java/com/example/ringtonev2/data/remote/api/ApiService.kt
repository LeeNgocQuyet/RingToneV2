package com.example.ringtonev2.data.remote.api

import com.example.ringtonev2.data.remote.dto.TikTokResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    /**
     * Lấy thông tin video TikTok (bao gồm URL audio).
     *
     * Endpoint: GET https://api-project2.h5cdn.com/api/ringtone/tiktok?url=<link>
     *
     * Trả về [TikTokResponse]; URL audio nằm ở `response.data.data.music`.
     */
    @GET("api/ringtone/tiktok")
    suspend fun getAudio(
        @Query("url") url: String
    ): TikTokResponse?
}

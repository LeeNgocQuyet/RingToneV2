package com.example.ringtonev2.data.remote.api

import com.example.ringtonev2.data.remote.dto.CategoryResponse
import com.example.ringtonev2.data.remote.dto.RingtoneResponse
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

    @GET("api/ringtone/list")
    suspend fun getRingtones(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 24,
        @Query("order_by") orderBy: String = "id",
        @Query("order") order: String = "asc",
        @Query("category_ids") categoryIds: String? = null,
        @Query("search") search: String? = null,
        @Query("list_ids") listIds: String? = null,
        @Query("is_clone") isClone: Boolean? = null,
    ): RingtoneResponse

    @GET("api/ringtone/categories/list")
    suspend fun getCategories(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 24,
        @Query("order_by") orderBy: String = "id",
        @Query("order") order: String = "asc",
    ): CategoryResponse

}

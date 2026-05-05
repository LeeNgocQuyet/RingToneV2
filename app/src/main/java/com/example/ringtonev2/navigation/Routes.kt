package com.example.ringtonev2.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

object Routes {
    @Serializable data object SplashRoute : NavKey
    @Serializable data object OnboardingRoute : NavKey
    @Serializable data object LanguageRoute : NavKey
    @Serializable data object MainRoute : NavKey

    @Serializable data class PlayerRoute(val ringtoneId: String) : NavKey

    @Serializable data object ExtractRoute : NavKey
    @Serializable data object ExtractionHistoryRoute : NavKey
    @Serializable data class ExtractingRoute(val url: String) : NavKey
    @Serializable data class GetInfoRoute(val filePath: String) : NavKey
    @Serializable data class AudioPreviewRoute(val filePath: String, val title: String) : NavKey
    
    @Serializable data class CategoryDetailRoute(val categoryId: String) : NavKey

    /** JSON của [com.example.ringtonev2.data.remote.dto.TikTokData] (Gson). */
    @Serializable data class AudioInfoRoute(val tikTokDataJson: String) : NavKey
    @Serializable data class AudioDownloadRoute(val tikTokDataJson: String) : NavKey
    @Serializable data object AudioErrorRoute : NavKey

}
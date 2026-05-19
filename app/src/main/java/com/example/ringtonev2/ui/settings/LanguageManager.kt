package com.example.ringtonev2.ui.settings

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

object LanguageManager {

    private val _currentLanguage = MutableStateFlow(getCurrentLanguage())
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    fun getCurrentLanguage(): String {
        val appLocale = AppCompatDelegate.getApplicationLocales().get(0)
        return appLocale?.language ?: Locale.getDefault().language
    }

    fun setLanguage(context: Context, languageCode: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.getSystemService(LocaleManager::class.java).applicationLocales =
                LocaleList.forLanguageTags(languageCode)
        } else {
            AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.forLanguageTags(languageCode)
            )
        }

        _currentLanguage.value = languageCode
    }

    fun getAvailableLanguages(): List<Language> = listOf(
        Language(
            code = "en",
            name = "English",
            nativeName = "English",
            flag = "🇺🇸"
        ),
        Language(
            code = "vi",
            name = "Vietnamese",
            nativeName = "Tiếng Việt",
            flag = "🇻🇳"
        )
    )

    fun getLanguageName(code: String): String {
        return getAvailableLanguages().find { it.code == code }?.nativeName ?: "English"
    }
}


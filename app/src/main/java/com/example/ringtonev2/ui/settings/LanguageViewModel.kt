package com.example.ringtonev2.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LanguageViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
) : ViewModel() {

    val currentLanguage: StateFlow<String> = LanguageManager.currentLanguage
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = LanguageManager.getCurrentLanguage()
        )

    val availableLanguages: List<Language> = LanguageManager.getAvailableLanguages()

    fun changeLanguage(languageCode: String) {
        LanguageManager.setLanguage(appContext, languageCode)
    }
}
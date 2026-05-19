package com.example.ringtonev2.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import java.util.Locale

private val Context.dataStore by preferencesDataStore(name = "app_prefs")

class DataStoreManager(private val context: Context) {
    object PrefKeys {
        val ONBOARDING_SHOWN = booleanPreferencesKey("onboarding_shown")
        val NOTIFICATION_CARD_COUNT = intPreferencesKey("notification_card_count")
        val SEARCH_HISTORY = stringPreferencesKey("search_history")
    }

    val onboardingShownFlow: Flow<Boolean> =
        context.dataStore.data.map { prefs ->
            prefs[PrefKeys.ONBOARDING_SHOWN] ?: false
        }
    suspend fun setOnboardingShown() {
        context.dataStore.edit { prefs ->
            prefs[PrefKeys.ONBOARDING_SHOWN] = true
        }
    }
    val notificationCardCountFlow: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[PrefKeys.NOTIFICATION_CARD_COUNT] ?: 0
    }
    suspend fun incrementNotificationCardCount() {
        context.dataStore.edit { prefs ->
            val current = prefs[PrefKeys.NOTIFICATION_CARD_COUNT] ?: 0
            prefs[PrefKeys.NOTIFICATION_CARD_COUNT] = current + 1
        }
    }
    // ToDo Cái này không Lưu vào Room Database ??? Cái này không có giới hạn số lần lưu à

    val searchHistoryFlow: Flow<List<String>> =
        context.dataStore.data.map { prefs ->
            prefs[PrefKeys.SEARCH_HISTORY]?.let { value ->
                runCatching {
                    val array = JSONArray(value)
                    List(array.length()) { index -> array.getString(index) }
                }.getOrDefault(emptyList())
            } ?: emptyList()
        }

    suspend fun setSearchHistory(history: List<String>) {
        context.dataStore.edit { prefs ->
            prefs[PrefKeys.SEARCH_HISTORY] = JSONArray(history).toString()
        }
    }
}

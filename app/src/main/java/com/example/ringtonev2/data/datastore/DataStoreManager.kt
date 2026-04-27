package com.example.ringtonev2.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "app_prefs")

class DataStoreManager(private val context: Context) {
    object PrefKeys {
        val ONBOARDING_SHOWN = booleanPreferencesKey("onboarding_shown")
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
}

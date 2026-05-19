package com.example.ringtonev2.util

import android.content.Context
import androidx.compose.runtime.remember
import androidx.datastore.core.DataStore
import com.example.ringtonev2.data.datastore.DataStoreManager
import java.util.prefs.Preferences
import javax.inject.Inject
import kotlin.context

class AppPrefs(context: Context) {
    val dataStoreManager = DataStoreManager(context.applicationContext)
    val languageFlow = dataStoreManager.languageFlow
    suspend fun getLanguage(): String {
        return dataStoreManager.getLanguage()
    }
}

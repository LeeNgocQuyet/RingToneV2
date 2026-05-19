package com.example.ringtonev2.util

import android.content.Context
import com.example.ringtonev2.data.datastore.DataStoreManager

class AppPrefs(context: Context) {
    val dataStoreManager = DataStoreManager(context.applicationContext)
    val languageFlow = dataStoreManager.languageFlow
    suspend fun getLanguage(): String {
        return dataStoreManager.getLanguage()
    }
}

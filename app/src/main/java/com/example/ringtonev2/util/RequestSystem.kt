package com.example.ringtonev2.util

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.core.net.toUri

fun canWriteSettings(context: Context): Boolean {
    return Settings.System.canWrite(context)
}

fun requestWriteSettingsPermission(context: Context) {
    val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).apply {
        data = "package:${context.packageName}".toUri()
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
}

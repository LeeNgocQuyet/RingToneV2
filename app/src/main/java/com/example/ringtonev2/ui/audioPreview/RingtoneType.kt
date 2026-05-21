package com.example.ringtonev2.ui.audioPreview

import android.media.RingtoneManager

enum class RingtoneType(val value: Int) {
    RINGTONE(RingtoneManager.TYPE_RINGTONE),
    NOTIFICATION(RingtoneManager.TYPE_NOTIFICATION),
    ALARM(RingtoneManager.TYPE_ALARM)
}
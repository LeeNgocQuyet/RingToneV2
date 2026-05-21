package com.example.ringtonev2.util

fun Long.formatDurationSecond(): String {
    if (this <= 0L) return "00:00"

    val minutes = this / 60
    val secs = this % 60

    return "%02d:%02d".format(minutes, secs)
}

fun Long.formatDurationMillisecond(): String {
    if (this <= 0L) return "00:00"

    val totalSeconds = this / 1000
    val minutes = totalSeconds / 60
    val secs = totalSeconds % 60

    return "%02d:%02d".format(minutes, secs)
}

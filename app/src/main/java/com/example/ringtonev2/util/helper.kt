package com.example.ringtonev2.util

import androidx.compose.foundation.clickable
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.navigation.NavController

// Todo ????
fun Modifier.debounceClickable(
    enabled: Boolean = true,
    interval: Long = 600L,
    onClick: () -> Unit
): Modifier = composed {
    var lastClickTime by remember { mutableLongStateOf(0L) }

    clickable(enabled = enabled) {
        val now = System.currentTimeMillis()
        if (now - lastClickTime > interval) {
            lastClickTime = now
            onClick()
        }
    }
}
fun NavController.safePopBackStack(): Boolean {
    return try {
        if (currentBackStackEntry != null && previousBackStackEntry != null) {
            popBackStack()
        } else {
            false
        }
    } catch (e: Exception) {
        false
    }
}
package com.example.ringtonev2.ui.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onOnboarding: () -> Unit,
    onMain: () -> Unit,
) {
    LaunchedEffect(Unit) {
        delay(2000)
        // logic to decide whether to go to onboarding or main
        onOnboarding()
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Splash Screen")
    }
}

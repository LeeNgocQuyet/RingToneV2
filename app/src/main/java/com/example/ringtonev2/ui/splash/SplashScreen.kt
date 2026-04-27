package com.example.ringtonev2.ui.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.dataStore
import com.example.ringtonev2.data.datastore.DataStoreManager
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onOnboarding: () -> Unit,
    onMain: () -> Unit,
) {
    val context = LocalContext.current
    val datastoreManager = DataStoreManager(context)

    val isOnboardingShown by datastoreManager.onboardingShownFlow
        .collectAsState(initial = false)

    LaunchedEffect(isOnboardingShown) {
        delay(2000)
        if (isOnboardingShown) {
            onMain()
        } else {
            onOnboarding()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Red), contentAlignment = Alignment.Center) {
        Text(text = "Splash Screen")
    }
}

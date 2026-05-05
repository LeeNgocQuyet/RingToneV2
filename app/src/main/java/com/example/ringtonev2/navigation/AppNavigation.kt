package com.example.ringtonev2.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.ringtonev2.navigation.Routes.SplashRoute
import com.example.ringtonev2.navigation.Routes.OnboardingRoute
import com.example.ringtonev2.navigation.Routes.MainRoute
import com.example.ringtonev2.navigation.Routes.PlayerRoute
import com.example.ringtonev2.navigation.Routes.ExtractRoute
import com.example.ringtonev2.navigation.Routes.ExtractionHistoryRoute
import com.example.ringtonev2.navigation.Routes.AudioInfoRoute
import com.example.ringtonev2.data.remote.dto.TikTokData
import com.example.ringtonev2.ui.audioInfo.AudioInfoScreen
import com.example.ringtonev2.ui.home.MainScreen
import com.google.gson.Gson
import com.example.ringtonev2.ui.onboarding.OnboardingScreen
import com.example.ringtonev2.ui.splash.SplashScreen

@Composable
fun AppNavigation() {
    val backStack = rememberNavBackStack(SplashRoute)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<SplashRoute> {
                SplashScreen(
                    onOnboarding = {
                        backStack.clear()
                        backStack.add(OnboardingRoute)
                    },
                    onMain = {
                        backStack.clear()
                        backStack.add(MainRoute)
                    },
                )
            }
            entry<OnboardingRoute> {
                OnboardingScreen(
                    onDone = {
                        backStack.clear()
                        backStack.add(MainRoute)
                    },
                )
            }

            entry<MainRoute> {
                MainScreen(
                    onOpenPlayer = { id -> backStack.add(PlayerRoute(id)) },
                    onOpenExtract = { backStack.add(ExtractRoute) },
                    onOpenHistory = { backStack.add(ExtractionHistoryRoute) },
                    onOpenAudioInfo = { data ->
                        backStack.add(AudioInfoRoute(Gson().toJson(data)))
                    },
                )
            }

            entry<AudioInfoRoute> { route ->
                val gson = remember { Gson() }
                val data = remember(route.tikTokDataJson) {
                    gson.fromJson(route.tikTokDataJson, TikTokData::class.java)
                }
                AudioInfoScreen(
                    data = data,
                    onBack = { backStack.removeLastOrNull() },
                )
            }
        },
    )
}

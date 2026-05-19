package com.example.ringtonev2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.ringtonev2.util.NetworkMonitor
import com.example.ringtonev2.navigation.AppNavigation
import com.example.ringtonev2.ui.theme.RingtoneTheme
import com.example.ringtonev2.components.EnableNotificationCard
import dagger.hilt.android.AndroidEntryPoint
import com.example.ringtonev2.util.AppPrefs
import androidx.compose.ui.platform.LocalLocale
import com.example.ringtonev2.util.AppLocaleProvider


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var appPrefs: AppPrefs



    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val networkMonitor = NetworkMonitor(applicationContext)
        appPrefs = AppPrefs(applicationContext)
        setContent {
            val lang by appPrefs.languageFlow.collectAsState(
                initial = LocalLocale.current.platformLocale.language
            )
            RingtoneTheme {
                AppLocaleProvider(lang) {
                    val isOnline by networkMonitor.isOnline.collectAsState(initial = true)
                    AppNavigation()
                    if (!isOnline) {

                        EnableNotificationCard(
                            title = stringResource(R.string.connection_timeout),
                            description = stringResource(R.string.connection_timeout_description),
                            buttonTitle = "OK",
                            onClick = {  },
                            painter = painterResource(id = R.drawable.network_notification),
                        )
                    }
                }
            }
        }
    }
}
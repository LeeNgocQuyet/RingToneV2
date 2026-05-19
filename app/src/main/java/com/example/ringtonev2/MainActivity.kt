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


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val networkMonitor = NetworkMonitor(applicationContext)
        setContent {
            RingtoneTheme {
                val isOnline by networkMonitor.isOnline.collectAsState(initial = true)
                AppNavigation()
                if (!isOnline) {
                    EnableNotificationCard(
                        title = stringResource(R.string.connection_timeout),
                        description = stringResource(R.string.connection_timeout_description),
                        buttonTitle = "OK",
                        onClick = { },
                        painter = painterResource(id = R.drawable.network_notification),
                    )
                }
            }
        }
    }
}
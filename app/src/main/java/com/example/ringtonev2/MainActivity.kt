package com.example.ringtonev2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.copy
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.ringtonev2.music.util.NetworkMonitor
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
                        onClick = {  },
                        onDismiss = {  },
                        painter = painterResource(id = R.drawable.network_notification),
                    )
                }
            }
        }
    }
}

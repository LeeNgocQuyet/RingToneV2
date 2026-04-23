package com.example.ringtonev2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.ringtonev2.navigation.AppNavigation
import com.example.ringtonev2.ui.theme.RingtoneTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RingtoneTheme {
                AppNavigation()
            }
        }
    }
}

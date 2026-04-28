package com.example.ringtonev2.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import com.example.ringtonev2.components.EnableNotificationCard
import com.example.ringtonev2.ui.category.CategoryScreen
import com.example.ringtonev2.ui.download.DownloadScreen
import com.example.ringtonev2.ui.playlist.PlayListScreen
import com.example.ringtonev2.ui.ringtone.RingtoneScreen
import com.example.ringtonev2.ui.settings.SettingsScreen
import com.example.ringtonev2.R
import com.google.android.material.snackbar.Snackbar
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat.startActivity
import android.provider.Settings

private enum class MainTab(val title: String, val icon: ImageVector) {
    Home("Home", Icons.Default.Home),
    Download("Download", Icons.Default.Download),
    Category("Category", Icons.Default.Category),
    Playlist("Playlist", Icons.Default.LibraryMusic),

    Setting("Settings", Icons.Default.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onOpenPlayer: (String) -> Unit,
    onOpenExtract: () -> Unit,
    onOpenHistory: () -> Unit,
) {
    val context = LocalContext.current
    var tab by rememberSaveable { mutableStateOf(MainTab.Home) }

    var showPermissionDialog by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            showPermissionDialog = true
        }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val isGranted = ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!isGranted) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    if (showPermissionDialog) {
        Dialog(onDismissRequest = { showPermissionDialog = false }) {
            EnableNotificationCard(
                painter = painterResource(id = R.drawable.enable_notification),
                title = stringResource(id = R.string.enable_notifications),
                description = stringResource(id = R.string.enable_notifications_description),
                buttonTitle = "Go to Settings",
                onClick = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                    showPermissionDialog = false
                },
                onDismiss = { showPermissionDialog = false }
            )
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Main Screen") },
                actions = {
                    IconButton(onClick = {
                        tab = MainTab.Setting
                    }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                MainTab.entries
                    .filter { it != MainTab.Setting }
                    .forEach { entry ->
                    NavigationBarItem(
                        selected = tab == entry,
                        onClick = { tab = entry },
                        icon = { Icon(entry.icon, contentDescription = null) },
                        label = { Text(entry.title) },
                    )
                }
            }
        },
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {
            when (tab) {
                MainTab.Home -> RingtoneScreen()
                MainTab.Download -> DownloadScreen(onOpenPlayer = onOpenPlayer)
                MainTab.Category -> CategoryScreen(onOpenPlayer = onOpenPlayer)
                MainTab.Playlist -> PlayListScreen(onOpenPlayer = onOpenPlayer)

                MainTab.Setting -> SettingsScreen(onOpenPlayer = onOpenPlayer, onBack = {
                    tab = MainTab.Home
                })
            }
        }
    }
}
/*
private fun askNotificationPermission() {
    // This is only necessary for API level >= 33 (TIRAMISU)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
        ) {
//                Log.e(TAG, "PERMISSION_GRANTED")
            // FCM SDK (and your app) can post notifications.
        } else {
//                Log.e(TAG, "NO_PERMISSION")
            // Directly ask for the permission
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}

private val requestPermissionLauncher = registerForActivityResult(
    ActivityResultContracts.RequestPermission()
) { isGranted: Boolean ->
    if (isGranted) {
        Toast.makeText(this, "Notifications permission granted", Toast.LENGTH_SHORT)
            .show()
    } else {
//            Toast.makeText(
//                this, "${getString(R.string.app_name)} can't post notifications without Notification permission",
//                Toast.LENGTH_LONG
//            ).show()

        Snackbar.make(
                MainScreen.containermain,
            String.format(
                String.format(
                    getString(R.string.txt_error_post_notification),
                    getString(R.string.app_name)
                )
            ),
            Snackbar.LENGTH_INDEFINITE
        ).setAction(getString(R.string.goto_settings)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val settingsIntent: Intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                startActivity(settingsIntent)
            }
        }.show()
    }
}
*/

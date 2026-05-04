package com.example.ringtonev2.ui.home

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import com.example.ringtonev2.components.EnableNotificationCard
import com.example.ringtonev2.ui.category.CategoryScreen
import com.example.ringtonev2.ui.download.DownloadScreen
import com.example.ringtonev2.ui.playlist.PlayListScreen
import com.example.ringtonev2.ui.ringtone.RingtoneScreen
import com.example.ringtonev2.R
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import android.provider.Settings
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ringtonev2.data.datastore.DataStoreManager
import com.example.ringtonev2.ui.theme.AppTypography
import kotlinx.coroutines.launch

private enum class MainTab(val title: String, @DrawableRes val icon: Int) {
    Home("Home", R.drawable.home),
    Download("Download", R.drawable.download),
    Category("Category", R.drawable.category),
    Playlist("Playlist", R.drawable.playlist),

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
    var link by remember { mutableStateOf("") }
    var showPermissionDialog by remember { mutableStateOf(false) }
    val settingsManager = remember { DataStoreManager(context) }
    val cardShownCount by settingsManager.notificationCardCountFlow.collectAsState(initial = -1)
    val scope = rememberCoroutineScope()
    var hasHandledThisState by remember { mutableStateOf(false) }
    val activity = context as? Activity
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted && activity != null) {

            val shouldShow = ActivityCompat.shouldShowRequestPermissionRationale(
                activity, Manifest.permission.POST_NOTIFICATIONS
            )

            if (shouldShow && cardShownCount < 2) {
                showPermissionDialog = true
            }
        }
    }

    LaunchedEffect(cardShownCount) {
        if (cardShownCount == -1) return@LaunchedEffect

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && activity != null) {
            val isGranted = ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!isGranted && cardShownCount < 2) {
                val shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                    activity, Manifest.permission.POST_NOTIFICATIONS
                )

                if (shouldShowRationale) {
                    if (!hasHandledThisState) {
                        showPermissionDialog = true
                        hasHandledThisState = true
                    }
                } else {
                    if (cardShownCount == 0 && !hasHandledThisState) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        hasHandledThisState = true
                    }
                }
            }
        }
    }

    if (showPermissionDialog) {
        Dialog(onDismissRequest = {

            showPermissionDialog = false
            scope.launch {
                settingsManager.incrementNotificationCardCount()
            }
        }) {
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
                onDismiss = {
                    showPermissionDialog = false
                    hasHandledThisState = false
                }
            )
        }
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )
            MainTopBar(
                isSearchIcon = (tab == MainTab.Download),
                title = stringResource(id = R.string.app_name),
                onSearchClick = { },
                onSettingsClick = { }
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(84.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            ) {

                NavigationBar {
                    MainTab.entries
                        .forEach { entry ->
                            NavigationBarItem(
                                selected = tab == entry,
                                onClick = { tab = entry },
                                icon = {
                                    Icon(
                                        painter = painterResource(id = entry.icon),
                                        contentDescription = entry.title
                                    )
                                },
                                label = {
                                    Text(
                                        text = entry.title,
                                        style = AppTypography.bodyMedium.copy(
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.W600
                                        ),
                                        color = if (tab == entry)
                                            colorResource(R.color.text_bar_selected)
                                        else
                                            colorResource(R.color.text_bar_default)
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = colorResource(R.color.icon_bar_selected),
                                    unselectedIconColor = colorResource(R.color.icon_bar_default),
                                    selectedTextColor = colorResource(R.color.text_bar_selected),
                                    unselectedTextColor = colorResource(R.color.text_bar_default),
                                    indicatorColor = Color.Transparent
                                )
                            )
                        }
                }
            }

        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (tab) {
                MainTab.Home -> RingtoneScreen()
                MainTab.Download -> DownloadScreen(
                    onOpenPlayer = onOpenPlayer,
                    link = link,
                    onLinkChange = { link = it},
                    )
                MainTab.Category -> CategoryScreen(onOpenPlayer = onOpenPlayer)
                MainTab.Playlist -> PlayListScreen(onOpenPlayer = onOpenPlayer)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(
    isSearchIcon: Boolean,
    title: String,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    TopAppBar(
        title = {
            Box(
                modifier = Modifier
                    .width(254.dp)
                    .height(30.dp)
            )
            {
                Text(
                    text = title,
                    style = AppTypography.titleMedium.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.W600
                    ),
                    color = colorResource(R.color.content_brand),
                )
            }
        },
        actions = {
            if (!isSearchIcon) {
                IconButton(
                    onClick = onSearchClick,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(colorResource(R.color.content_subtlest))
                ) {
                    Icon(
                        painter = painterResource(R.drawable.search),
                        contentDescription = "Search",
                        tint = Color.White,
                    )
                }
            }
            Spacer(modifier = Modifier.width(24.dp))
            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(colorResource(R.color.content_subtlest))
            ) {
                Icon(
                    painter = painterResource(R.drawable.setting),
                    contentDescription = "Settings",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Black
        )
    )
}
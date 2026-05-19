package com.example.ringtonev2.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ringtonev2.R
import com.example.ringtonev2.components.BackNavigationIconButton
import com.example.ringtonev2.ui.theme.SoftPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: LanguageViewModel = viewModel()
) {
    val context = LocalContext.current
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    var showLanguageBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black
                ),
                navigationIcon = {
                    BackNavigationIconButton(onClick = onBack)
                },
                title = {
                    Text(
                        text = stringResource(R.string.settings_title),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // General Settings Section
            SettingsSection {
                SettingItem(
                    icon = Icons.Outlined.Language,
                    title = stringResource(R.string.settings_language),
                    subtitle = LanguageManager.getLanguageName(context, currentLanguage),
                    onClick = { showLanguageBottomSheet = true }
                )
                SettingItem(
                    icon = Icons.Outlined.StarOutline,
                    title = stringResource(R.string.settings_rating_us),
                    onClick = { /* TODO: Open Play Store */ }
                )
                SettingItem(
                    icon = Icons.Outlined.Feedback,
                    title = stringResource(R.string.settings_feedback),
                    onClick = { /* TODO: Open feedback */ },
                    showDivider = false
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Other Settings Section
            SettingsSection {
                SettingItem(
                    icon = Icons.Outlined.Share,
                    title = stringResource(R.string.settings_share_app),
                    onClick = { /* TODO: Share app */ }
                )
                SettingItem(
                    icon = Icons.Outlined.Description,
                    title = stringResource(R.string.settings_term_of_use),
                    onClick = { /* TODO: Open terms */ }
                )
                SettingItem(
                    icon = Icons.Outlined.Policy,
                    title = stringResource(R.string.settings_privacy_policy),
                    onClick = { /* TODO: Open privacy */ },
                    showDivider = false
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // App Version
            Text(
                text = stringResource(R.string.settings_app_version),
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
    }

    // Language Bottom Sheet (Modern UI)
    if (showLanguageBottomSheet) {
        LanguageBottomSheet(
            currentLanguage = currentLanguage,
            availableLanguages = viewModel.availableLanguages,
            onLanguageSelected = { language ->
                viewModel.changeLanguage(language.code)
                showLanguageBottomSheet = false
            },
            onDismiss = { showLanguageBottomSheet = false }
        )
    }
}

@Composable
private fun SettingsSection(
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1A)
        ),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            content()
        }
    }
}

@Composable
fun SettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit,
    showDivider: Boolean = true
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = SoftPurple,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = subtitle,
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }

        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 0.5.dp,
                color = Color.DarkGray.copy(alpha = 0.3f)
            )
        }
    }
}
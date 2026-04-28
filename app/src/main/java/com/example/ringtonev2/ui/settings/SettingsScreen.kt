package com.example.ringtonev2.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Policy
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.TextSnippet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ringtonev2.data.datastore.DataStoreManager
import com.example.ringtonev2.ui.theme.RingtoneTheme
import com.example.ringtonev2.ui.theme.SoftPurple
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onOpenPlayer: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dataStoreManager = remember { DataStoreManager(context) }
    val currentLanguage by dataStoreManager.languageFlow.collectAsState(initial = "en")

    var showLanguageDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                title = {
                    Text(
                        text = "Setting",
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

            // First Section
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    SettingItem(
                        icon = Icons.Outlined.Language,
                        title = "Language",
                        subtitle = if (currentLanguage == "vi") "Tiếng Việt" else "English",
                        onClick = { showLanguageDialog = true }
                    )
                    SettingItem(
                        icon = Icons.Outlined.StarOutline,
                        title = "Rating Us",
                        onClick = {  }
                    )
                    SettingItem(
                        icon = Icons.Default.ChatBubbleOutline,
                        title = "Feedback",
                        onClick = {  },
                        showDivider = false
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    SettingItem(
                        icon = Icons.Outlined.Share,
                        title = "Share App",
                        onClick = { /* Handle Share */ }
                    )
                    SettingItem(
                        icon = Icons.Outlined.TextSnippet,
                        title = "Term Of Use",
                        onClick = {  }
                    )
                    SettingItem(
                        icon = Icons.Outlined.Policy,
                        title = "Privacy Policy",
                        onClick = {  },
                        showDivider = false
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "App Version 1.0.0",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
    }

    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text("Select Language") },
            text = {
                Column {
                    LanguageOption("English", currentLanguage == "en") {
                        scope.launch {
                            dataStoreManager.setLanguage("en")
                            showLanguageDialog = false
                        }
                    }
                    LanguageOption("Tiếng Việt", currentLanguage == "vi") {
                        scope.launch {
                            dataStoreManager.setLanguage("vi")
                            showLanguageDialog = false
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text("Close")
                }
            }
        )
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
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
                Text(
                    text = subtitle,
                    color = Color.Gray,
                    fontSize = 12.sp
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
            color = Color.DarkGray.copy(alpha = 0.5f)
        )
    }
}

@Composable
fun LanguageOption(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = isSelected, onClick = onClick)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = name, color = if (isSelected) SoftPurple else Color.Black)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun SettingsScreenPreview() {
    RingtoneTheme {
        SettingsScreen(
            onOpenPlayer = {},onBack = {})
    }
}

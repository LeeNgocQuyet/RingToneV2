package com.example.ringtonev2.ui.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ringtonev2.R
import com.example.ringtonev2.domain.Category
import com.example.ringtonev2.domain.Ringtone
import com.example.ringtonev2.ui.theme.AppTypography

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onOpenPlayer: (String) -> Unit
) {

    val uiState by viewModel.homeState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.background_default))
    ) {

        if (uiState.isLoading) {

            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {

            item {
                Spacer(modifier = Modifier.height(8.dp))
                FeaturedBannerSection()
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))

                CategoryTabsSection(
                    categories = uiState.categories,
                    selectedCategoryId = uiState.selectedCategoryId,
                    onCategoryClick = {
                        viewModel.selectCategory(it)
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            items(uiState.ringtones) { ringtone ->

                MusicItemRow(
                    ringtone = ringtone,
                    onPlayClick = {
                        onOpenPlayer(ringtone.id.toString())
                    }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    thickness = 0.5.dp,
                    color = colorResource(id = R.color.border_subtlest)
                )
            }
        }
    }
}



@Composable
fun FeaturedBannerSection() {
    // Format Box border và background giống hệt DownloadScreen bạn gửi
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0x00CFA3FF), Color(0xFFCFA3FF))
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .clip(RoundedCornerShape(24.dp))
    ) {
        // Hình nền của Banner
        Image(
            painter = painterResource(id = R.drawable.bg_download_screen), // Dùng chung tài nguyên nền
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Nội dung Banner (Bên trái)
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(start = 24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Top Ringtone",
                style = AppTypography.titleLarge.copy(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
            Text(
                text = "Your phone, your vibe",
                style = AppTypography.bodyMedium.copy(
                    color = colorResource(id = R.color.content_subtlest)
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.background_secondary) // Màu tím nhạt
                ),
                shape = RoundedCornerShape(50),
                contentPadding = PaddingValues(horizontal = 20.dp)
            ) {
                Icon(Icons.Default.PlayArrow, null, tint = Color.Black)
                Text("Play", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }

        // Hộp nhạc rời (Vinyl) - Nằm bên phải và đè lên trên
        VinylComponent(Modifier.align(Alignment.CenterEnd))
    }
}

@Composable
fun VinylComponent(modifier: Modifier) {

    Box(
        modifier = modifier
            .offset(x = 10.dp)
            .size(130.dp)
            .rotate(5f)
            .clip(RoundedCornerShape(12.dp))
            .background(colorResource(id = R.color.border_bold)) // Màu xanh lơ của vỏ đĩa
            .padding(8.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize(),
            shape = CircleShape,
            color = Color.Black
        ) {
            Box(contentAlignment = Alignment.Center) {
                // Tâm đĩa than
                Box(
                    Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(colorResource(id = R.color.background_secondary))
                )
            }
        }
    }
}

@Composable
fun MusicItemRow(ringtone: Ringtone, onPlayClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Thumbnail
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(colorResource(id = R.color.border_bold)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_play), // Icon play của bạn
                contentDescription = null,
                tint = colorResource(id = R.color.content_brand)
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Tik Viral Hit 2024",
                style = AppTypography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = colorResource(id = R.color.content_secondary)
                )
            )
            Text(
                text = "00:30",
                style = AppTypography.bodySmall.copy(
                    color = colorResource(id = R.color.content_disabled)
                )
            )
        }

        // Nút Set
        Button(
            onClick = { },
            modifier = Modifier.height(32.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.background_secondary)),
            shape = RoundedCornerShape(50)
        ) {
            Text("Set", style = AppTypography.labelMedium.copy(color = Color.Black))
        }

        Spacer(modifier = Modifier.width(12.dp))

        Icon(
            painter = painterResource(id = R.drawable.ic_favourite),
            contentDescription = null,
            tint = Color.Red,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun CategoryTabsSection(
    categories: List<Category>,
    selectedCategoryId: Int?,
    onCategoryClick: (Int?) -> Unit
) {

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        categories.forEach { category ->

            val isSelected = category.id == selectedCategoryId

            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(
                        if (isSelected)
                            colorResource(id = R.color.content_secondary)
                        else
                            colorResource(id = R.color.border_bold)
                    ),

                onClick = {
                    onCategoryClick(category.id)
                }
            ) {

                Text(
                    text = category.name,
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    ),
                    style = AppTypography.labelMedium.copy(
                        color =
                            if (isSelected)
                                Color.Black
                            else
                                colorResource(id = R.color.content_disabled)
                    )
                )
            }
        }
    }
}


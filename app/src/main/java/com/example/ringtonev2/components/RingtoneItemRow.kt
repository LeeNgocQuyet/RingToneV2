package com.example.ringtonev2.components

import com.example.ringtonev2.ui.theme.*

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ringtonev2.R
import com.example.ringtonev2.domain.Ringtone
import com.example.ringtonev2.ui.theme.AppTypography
import com.example.ringtonev2.util.formatDurationMillisecond

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RingtoneItemRow(
    ringtone: Ringtone,
    isPlaying: Boolean,
    onPlayClick: () -> Unit,
    onSetClick: () -> Unit,
    onFavorite: () -> Unit,
    isFavorite: Boolean
) {
    val offsetX = remember { Animatable(0f) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isPlaying)
                Brush.horizontalGradient(
                    colors = listOf(
                        ContentBrand.copy(alpha = 0.24f),
                        Black
                    )
                )
                else Brush.horizontalGradient(
                    colors = listOf(
                        Black,
                        Black
                    )
                )
            )
            .offset { IntOffset(offsetX.value.toInt(), 0) }
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Black),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bg_ringtone),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.75f)),
                    contentAlignment = Alignment.Center
                ) {

                    IconButton(
                        onClick = onPlayClick,
                        modifier = Modifier.size(24.dp),
                        content = {
                            Icon(
                                painter = if (!isPlaying) painterResource(id = R.drawable.ic_play)
                                else painterResource(id = R.drawable.ic_pause),
                                contentDescription = null,
                                tint = ContentBrand,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                }
            }

            //name + duration
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = ringtone.name ?: "Unknown",
                    style = AppTypography.labelLarge.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W600,
                        color = if (!isPlaying) ContentDefault
                        else ContentBrand
                    )
                )
                Text(
                    text = ringtone.duration.formatDurationMillisecond(),
                    style = AppTypography.bodySmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.W500,
                        color = ContentSubtlest
                    )
                )
            }

            // Set button
            Button(
                onClick = {
                    onSetClick()
                },
                modifier = Modifier.height(32.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BackgroundSecondary),
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    text = stringResource(R.string.set_button),
                    style = AppTypography.bodySmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.W500,
                        color = ContentOnSecondary
                    )
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.12f))
                    .clickable { onFavorite() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(
                        if (isFavorite) R.drawable.ic_favorite_fullfill
                        else R.drawable.ic_favorite
                    ),
                    contentDescription = null,
                    tint = if (isFavorite) Color.Red else Color.White
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier.align(Alignment.BottomCenter),
            thickness = 1.dp,
            color = BorderBold.copy(alpha = 0.7f)
        )
    }
}
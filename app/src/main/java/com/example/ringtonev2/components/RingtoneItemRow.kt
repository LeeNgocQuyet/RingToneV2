package com.example.ringtonev2.components

import com.example.ringtonev2.ui.theme.*

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ringtonev2.R
import com.example.ringtonev2.domain.Ringtone
import com.example.ringtonev2.ui.audioPreview.formatDurationMilisecond
import com.example.ringtonev2.ui.theme.AppTypography
import com.example.ringtonev2.ui.theme.White
import kotlinx.coroutines.launch
import okhttp3.internal.concurrent.formatDuration

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
            .offset { IntOffset(offsetX.value.toInt(), 0) }
    ) {

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
                    .background(Black),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bg_removal),
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
                        color = ContentDefault
                    )
                )
                Text(
                    text = formatDurationMilisecond(ringtone.duration),
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
                    Log.d("RingtoneItemRow", "BUTTON CLICK ${ringtone.id}")

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

            IconButton(
                onClick = onFavorite,
                modifier = Modifier.size(30.dp).background(
                    color = Color.White.copy(alpha = 0.12f),
                    shape = CircleShape
                ),

                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Transparent

                )
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
    }
}

fun formatDuration(milisecond: Int?): String {
    if (milisecond == null || milisecond <= 0L) return "00:00"
    val seconds = milisecond / 1000L
    val minutes = seconds / 60
    val secs = seconds % 60

    return "%02d:%02d".format(minutes, secs)
}
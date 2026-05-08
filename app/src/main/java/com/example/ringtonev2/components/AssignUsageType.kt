package com.example.ringtonev2.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ringtonev2.R
import com.example.ringtonev2.ui.theme.AppTypography

@Composable
fun AssignUsageDialog(
    onDismiss: () -> Unit,
    onRingtoneClick: () -> Unit,
    onNotifiClick: () -> Unit,
    onAlarmClick: () -> Unit,
) {

    Dialog(
        onDismissRequest = onDismiss
    ) {

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1E1E1E)
            )
        ) {

            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.cancel_01),
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    }
                }

                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text(
                        text = stringResource(R.string.assign_usage_type),

                        style = AppTypography.labelLarge.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W600
                        ),
                        color = colorResource(R.color.content_default),
                        modifier = Modifier.align(Alignment.Center)
                    )


                }

                Spacer(modifier = Modifier.height(20.dp))

                UsageTypeItem(
                    title = stringResource(R.string.ringtone_title),
                    icon = R.drawable.ic_calling,
                    backgroundColor = colorResource(R.color.background_brand),
                    borderColor = colorResource(R.color.background_brand),
                    onClick = onRingtoneClick
                )

                Spacer(modifier = Modifier.height(14.dp))

                UsageTypeItem(
                    title = stringResource(R.string.notify_title),
                    icon = R.drawable.ic_noti,
                    backgroundColor = colorResource(R.color.background_secondary),
                    borderColor = colorResource(R.color.background_secondary),
                    onClick = onNotifiClick
                )

                Spacer(modifier = Modifier.height(14.dp))

                UsageTypeItem(
                    title = stringResource(R.string.alarm_title),
                    icon = R.drawable.ic_alarm,
                    backgroundColor = Color(0xFFFF9D66),
                    borderColor = Color(0xFFFF9D66),
                    onClick = onAlarmClick
                )
            }
        }
    }
}

@Composable
private fun UsageTypeItem(
    title: String,
    icon: Int,
    backgroundColor: Color,
    borderColor: Color,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF2A2A2A))
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        borderColor.copy(alpha = 0.3f),
                        borderColor
                    )
                ),
                shape = RoundedCornerShape(18.dp)
            )
            .clickable {
                onClick()
            }
            .padding(
                horizontal = 16.dp,
                vertical = 18.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = title,
            style = AppTypography.labelLarge.copy(
                fontSize = 16.sp,
                fontWeight = FontWeight.W600
            ),
            color = colorResource(R.color.content_default),
        )
    }
}

@Preview
@Composable
fun PreviewAssignUsageDialog() {
    AssignUsageDialog(
        onDismiss = {},
        onRingtoneClick = {},
        onNotifiClick = {},
        onAlarmClick = {})
}
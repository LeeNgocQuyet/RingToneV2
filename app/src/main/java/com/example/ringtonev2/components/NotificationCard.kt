package com.example.ringtonev2.components

import com.example.ringtonev2.ui.theme.*

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ringtonev2.ui.theme.AppTypography

// ToDo Dùng chung Notification vs Mất mạng mà Đặt tên thế này à

@Composable
fun EnableNotificationCard(
    painter: Painter,
    title: String,
    description: String,
    buttonTitle: String,
    onClick: () -> Unit,

    ) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1E)),
        modifier = Modifier.wrapContentHeight().
        padding(16.dp)

        ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
                    .width(120.dp)
                    .height(120.dp)

            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = title,
                color = Color.White,

                style = AppTypography.titleMedium.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W700
                ),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                textAlign = TextAlign.Center,
                color = ContentSubtlest,
                style = AppTypography.titleSmall.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W500
                ),
                fontFamily = FontFamily.Default,
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                modifier = Modifier.fillMaxWidth().height(44.dp), onClick = onClick,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BackgroundBrand
                )

            ) {
                Text(
                    text = buttonTitle,
                    style = AppTypography.bodyMedium.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W600
                    ),
                    color = ContentSubtle
                )
            }
        }
    }
}
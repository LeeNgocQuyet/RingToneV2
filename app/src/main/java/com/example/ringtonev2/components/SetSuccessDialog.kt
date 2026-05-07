package com.example.ringtonev2.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun SetRingtoneSuccessDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1E1E1E)
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                

                // Nội dung chính
                Image(
                    painter = painterResource(id = R.drawable.image_success),
                    contentDescription = "Success Icon",
                    modifier = Modifier.size(100.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.success_title),
                    style = AppTypography.titleLarge.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.W700
                    ),
                    color = colorResource(R.color.content_default),

                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.success_description),
                    style = AppTypography.bodyMedium.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W500
                    ),
                    color = colorResource(R.color.content_subtlest)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD4FF71),
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(27.dp)
                ) {
                    Text(
                        text = stringResource(R.string.ok),
                        style = AppTypography.labelLarge.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W600
                        ),
                        color = colorResource(R.color.content_onsecondary)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewSetRingtoneSuccessDialog() {
    SetRingtoneSuccessDialog(onDismiss = {})
}
package com.example.ringtonev2.components

import com.example.ringtonev2.ui.theme.*

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ringtonev2.R
import com.example.ringtonev2.ui.theme.AppTypography
import com.example.ringtonev2.ui.theme.Delete_Color

@Composable
fun DeleteRingtoneDialog(onDismiss: () -> Unit, onDelete: () -> Unit) {
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
                Image(
                    painter = painterResource(id = R.drawable.image_delete),
                    contentDescription = "Delete Image",
                    modifier = Modifier.size(100.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.delete_ringtone),
                    style = AppTypography.titleLarge.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.W700
                    ),
                    color = ContentDefault,

                    )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.delete_ringtone_description),
                    style = AppTypography.bodyMedium.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W500
                    ),
                    color = ContentSubtle,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onDelete,
                        modifier = Modifier
                            .weight(1f)
                            .height(60.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Delete_Color,
                            contentColor = ContentSubtlest
                        ),
                        shape = RoundedCornerShape(30.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.delete_confirm),
                            style = AppTypography.labelLarge.copy(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.W600
                            ),
                            color = ContentSubtle
                        )
                    }

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(60.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(0.12f),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(30.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.delete_cancel),
                            style = AppTypography.labelLarge.copy(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.W600
                            ),
                            color = ContentSubtle
                        )
                    }
                }
            }
        }
    }
}
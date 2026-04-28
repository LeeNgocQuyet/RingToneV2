package com.example.ringtonev2.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ringtonev2.R
import com.example.ringtonev2.data.datastore.DataStoreManager
import com.example.ringtonev2.ui.theme.LimeGreen
import com.example.ringtonev2.ui.theme.SoftPurple
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    onDone: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val datastoreManager = remember { DataStoreManager(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Image(
            painter = painterResource(R.drawable.bg_ob),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = stringResource(R.string.slogan),
                color = LimeGreen,
                fontSize = 25.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 35.sp,
                modifier = Modifier.padding()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.onboarding_text),
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 24.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(all = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    scope.launch {
                        datastoreManager.setOnboardingShown()
                        onDone()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SoftPurple),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    text = stringResource(R.string.get_started),
                    color = Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

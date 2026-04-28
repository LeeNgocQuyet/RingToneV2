package com.example.ringtonev2.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ringtonev2.R
import com.example.ringtonev2.data.datastore.DataStoreManager
import com.example.ringtonev2.ui.theme.ContentBrand
import com.example.ringtonev2.ui.theme.ContentSubtlest
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onOnboarding: () -> Unit,
    onMain: () -> Unit,
) {
    val context = LocalContext.current
    val datastoreManager = DataStoreManager(context)

    val isOnboardingShown by datastoreManager.onboardingShownFlow
        .collectAsState(initial = false)

    LaunchedEffect(isOnboardingShown) {
        delay(2000)
        if (isOnboardingShown) {
            onMain()
        } else {
            onOnboarding()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.Black))
    ) {
        Image(
            painter = painterResource(id = R.drawable.ellipse_384),
            contentDescription = "purple",
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.3f)
                .blur(radius = 100.dp),
        )

        Image(
            painter = painterResource(id = R.drawable.ellipse_385),
            contentDescription = "yellow",
            modifier = Modifier
                .alpha(0.5f)
                .blur(radius = 100.dp),
        )

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(colorResource(id = R.color.background_neutral_bolder))
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.app_name),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp)
                .padding(horizontal = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = colorResource(R.color.accent),
                trackColor = ContentBrand
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.This_action_may_contain_ads),
                color = ContentSubtlest,
                fontSize = 12.sp
            )
        }
    }
}


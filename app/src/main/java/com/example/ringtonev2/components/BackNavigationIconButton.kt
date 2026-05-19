package com.example.ringtonev2.components

import com.example.ringtonev2.ui.theme.*

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.ringtonev2.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun BackNavigationIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String = stringResource(R.string.cd_back),
    debounceInterval: Long = 600L,
) {
    var lastClickTime by remember { mutableLongStateOf(0L) }

    IconButton(
        onClick = {
            val now = System.currentTimeMillis()

            if (now - lastClickTime > debounceInterval) {
                lastClickTime = now
                onClick()
            }
        },
        modifier = modifier
            .padding(start = 8.dp)
            .size(34.dp)
            .clip(CircleShape)
            .background(Accent)
    ) {

        Icon(
            painter = painterResource(R.drawable.ic_back),
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
    }
}

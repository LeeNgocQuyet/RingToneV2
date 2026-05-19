package com.example.ringtonev2.ui.category

import com.example.ringtonev2.ui.theme.*

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells.*
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ringtonev2.R
import com.example.ringtonev2.ui.theme.AppTypography

@SuppressLint("ShowToast")
@Composable
fun CategoryScreen(
    viewModel: CategoryScreenViewModel = hiltViewModel(),
    onOpenCategory: (String) -> Unit
) {
    val state by viewModel.categoryState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(state) {
        when (state) {
            is CategoryState.Error -> {
                val text = "Hello toast!"
                val duration = Toast.LENGTH_SHORT

                Toast.makeText(context, text, duration).show()
                viewModel.resetCategoryState()
            }
            else -> {
            }
        }
    }
    when (val s = state) {

        is CategoryState.Loading -> {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is CategoryState.Success -> {
            LazyVerticalGrid(
                columns = Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .background(Black)
                    .padding(horizontal = 14.dp),
                contentPadding = PaddingValues(
                    top = 12.dp,
                    bottom = 100.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                items(s.list) { category  ->

                    CategoryItem(
                        title = category.name,
                        amount = 0,
                        onClick = {
                            onOpenCategory(category.id.toString())
                        }
                    )
                }
            }
        }

        else -> {}
    }
}


@Composable
fun CategoryItem(
    title: String,
    amount: Int,
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                color = Color.White.copy(alpha = 0.1f),

                )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        ContentSecondary,
                        ContentSecondary
                            .copy(alpha = 0.4f)
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable {
                onClick()
            }
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(R.drawable.bg_category_item),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .padding(horizontal = 14.dp)
                    .padding(top = 10.dp)
            ) {

                Text(
                    text = title,
                    style = AppTypography.labelLarge.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W600
                    ),
                    color = ContentDefault
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = amount.toString(),
                    style = AppTypography.bodyMedium.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W500
                    ),
                    color = ContentSubtlest
                )
            }
        }
    }
}

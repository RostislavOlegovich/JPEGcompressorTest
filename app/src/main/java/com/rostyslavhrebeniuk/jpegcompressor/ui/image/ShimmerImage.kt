package com.rostyslavhrebeniuk.jpegcompressor.ui.image

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.rostyslavhrebeniuk.jpegcompressor.ui.helpers.shimmerBrush

@Composable
fun ShimmerImage(
    modifier: Modifier = Modifier,
    data: Any?,
) {
    val showShimmer = remember { mutableStateOf(true) }
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(data)
            .crossfade(enable = true)
            .build(),
        contentDescription = null,
        modifier = modifier
            .fillMaxSize()
            .background(shimmerBrush(targetValue = 1300f, showShimmer = showShimmer.value)),
//        contentScale = ContentScale.Fit,
        onError = { showShimmer.value = false},
        onSuccess = { showShimmer.value = false }
    )
}
package com.dummychallenge.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.data.config.ApiConfig

/**
 * Image component with fallback logic that shows default image on error.
 * Uses remember + mutableStateOf to manage local state within the composable.
 */
@Composable
fun ProfileImage(
    imageUrl: String?,
    contentDescription: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    // remember + mutableStateOf creates local state that persists across recompositions
    var currentImageUrl by remember { mutableStateOf(imageUrl ?: ApiConfig.DEFAULT_PROFILE_IMAGE_URL) }
    
    AsyncImage(
        model = currentImageUrl,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
        onError = {
            // Fallback to default image if current image fails to load
            if (currentImageUrl != ApiConfig.DEFAULT_PROFILE_IMAGE_URL) {
                currentImageUrl = ApiConfig.DEFAULT_PROFILE_IMAGE_URL
            }
        }
    )
}

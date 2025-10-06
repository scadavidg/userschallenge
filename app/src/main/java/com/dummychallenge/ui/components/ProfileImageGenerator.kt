package com.dummychallenge.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

/**
 * Component for generating profile images using DiceBear API
 * Generates images automatically based on user input
 */
@Composable
fun ProfileImageGenerator(
    firstName: String,
    lastName: String,
    onImageUrlChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val fullName = "$firstName $lastName".trim()
    val seed = if (fullName.length >= 2) fullName.replace(" ", "") else "nogender"
    val imageUrl = "https://api.dicebear.com/9.x/pixel-art/png?seed=$seed"
    
    // Update parent with current image URL
    onImageUrlChange(imageUrl)
    
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            // Profile Image
            AsyncImage(
                model = imageUrl,
                contentDescription = "Generated Profile Image",
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}

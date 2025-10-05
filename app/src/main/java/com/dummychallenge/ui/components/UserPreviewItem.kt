package com.dummychallenge.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.domain.models.UserPreview
import com.dummychallenge.ui.theme.DummyTheme
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@Composable
fun UserPreviewItem(
    user: UserPreview,
    onUserClick: (String) -> Unit,
    onEditClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val editSwipeAction = SwipeAction(
        onSwipe = { onEditClick(user.id) },
        icon = {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit",
                tint = Color.White,
                modifier = Modifier.padding(16.dp)
            )
        },
        background = Color.Blue
    )

    val deleteSwipeAction = SwipeAction(
        onSwipe = { onDeleteClick(user.id) },
        icon = {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = Color.White,
                modifier = Modifier.padding(16.dp)
            )
        },
        background = Color.Red
    )

    SwipeableActionsBox(
        startActions = listOf(editSwipeAction),
        endActions = listOf(deleteSwipeAction),
        modifier = modifier
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(12.dp),
            onClick = { onUserClick(user.id) }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProfileImage(
                    imageUrl = user.picture,
                    contentDescription = "User Image",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${user.firstName} ${user.lastName}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "ID: ${user.id}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Arrow indicator for detail
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "View Details",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserPreviewItemPreview() {
    DummyTheme {
        UserPreviewItem(
            user = UserPreview(
                id = "1",
                title = "mr",
                firstName = "John",
                lastName = "Doe",
                picture = "https://randomuser.me/api/portraits/men/1.jpg"
            ),
            onUserClick = {},
            onEditClick = {},
            onDeleteClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UserPreviewItemWithFallbackPreview() {
    DummyTheme {
        UserPreviewItem(
            user = UserPreview(
                id = "2",
                title = "ms",
                firstName = "Jane",
                lastName = "Smith",
                picture = "https://invalid-url-that-will-404.com/image.jpg"
            ),
            onUserClick = {},
            onEditClick = {},
            onDeleteClick = {}
        )
    }
}
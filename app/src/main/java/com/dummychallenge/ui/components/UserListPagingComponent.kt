package com.dummychallenge.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.domain.models.UserPreview

/**
 * Complex component with multiple parameters that handles pagination and different UI states.
 * This component demonstrates how to handle multiple states and callbacks in a reusable way.
 */
@Composable
fun UserListPagingComponent(
    users: List<UserPreview>,
    isLoading: Boolean,
    isLoadingMore: Boolean,
    hasMorePages: Boolean,
    error: String?,
    onUserClick: (String) -> Unit,
    onEditClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit = {},
    onCreateUser: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    // Derived state calculates when to load more data based on scroll position
    // This avoids unnecessary recompositions and only triggers when needed
    val shouldLoadMore = remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItemsNumber = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1

            lastVisibleItemIndex >= totalItemsNumber - 3 && hasMorePages && !isLoadingMore
        }
    }

    // LaunchedEffect triggers side effects when its key changes
    // Here it triggers loading more data when user scrolls near the bottom
    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value) {
            onLoadMore()
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        when {
            isLoading && users.isEmpty() -> {
                UserListSkeleton()
            }

            error != null && users.isEmpty() -> {
                ErrorState(
                    message = error,
                    onRetry = onRetry
                )
            }

            users.isEmpty() && !isLoading && error == null -> {
                EmptyState(
                    onActionClick = onCreateUser
                )
            }

            else -> {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = users,
                        key = { user -> user.id } // Key helps Compose identify items for efficient recomposition
                    ) { user ->
                        UserPreviewItem(
                            user = user,
                            onUserClick = onUserClick,
                            onEditClick = onEditClick,
                            onDeleteClick = onDeleteClick
                        )
                    }

                    if (isLoadingMore) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
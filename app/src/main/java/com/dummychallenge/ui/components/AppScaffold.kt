package com.dummychallenge.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dummychallenge.ui.theme.DummyTheme

/**
 * Enum to define screen types and their corresponding actions
 */
enum class ScreenType {
    USER_LIST,
    USER_DETAIL,
    CREATE_USER,
    EDIT_USER
}

/**
 * Data class to define bottom bar actions
 */
data class BottomBarAction(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val contentDescription: String,
    val onClick: () -> Unit,
    val backgroundColor: Color = Color.Unspecified,
    val contentColor: Color = Color.Unspecified
)

/**
 * Reusable scaffold component that provides:
 * - Toolbar with title and back button
 * - Bottom bar with centered floating actions
 * - Main screen content
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    screenType: ScreenType,
    title: String,
    onBackClick: (() -> Unit)? = null,
    onSaveClick: (() -> Unit)? = null,
    onDiscardClick: (() -> Unit)? = null,
    onAddUserClick: (() -> Unit)? = null,
    onEditClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    if (screenType != ScreenType.USER_LIST && onBackClick != null) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            when (screenType) {
                ScreenType.USER_DETAIL -> {
                    BottomActionBar(
                        actions = listOf(
                            BottomBarAction(
                                icon = Icons.Default.Edit,
                                contentDescription = "Edit User",
                                onClick = { onEditClick?.invoke() }
                            ),
                            BottomBarAction(
                                icon = Icons.Default.Delete,
                                contentDescription = "Delete User",
                                onClick = { onDeleteClick?.invoke() }
                            )
                        )
                    )
                }

                ScreenType.CREATE_USER -> {
                    BottomActionBar(
                        actions = listOf(
                            BottomBarAction(
                                icon = Icons.Default.Check,
                                contentDescription = "Save",
                                onClick = { onSaveClick?.invoke() }
                            ),
                            BottomBarAction(
                                icon = Icons.Default.Close,
                                contentDescription = "Discard",
                                onClick = { onDiscardClick?.invoke() }
                            )
                        )
                    )
                }

                ScreenType.EDIT_USER -> {
                    BottomActionBar(
                        actions = listOf(
                            BottomBarAction(
                                icon = Icons.Default.Check,
                                contentDescription = "Save",
                                onClick = { onSaveClick?.invoke() }
                            ),
                            BottomBarAction(
                                icon = Icons.Default.Close,
                                contentDescription = "Discard",
                                onClick = { onDiscardClick?.invoke() }
                            )
                        )
                    )
                }

                else -> {

                }
            }
        },
        floatingActionButton = {
            when (screenType) {
                ScreenType.USER_LIST -> {
                    onAddUserClick?.let { onClick ->
                        FloatingActionButton(
                            onClick = onClick,
                            containerColor = MaterialTheme.colorScheme.primary
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add User"
                            )
                        }
                    }
                }

                else -> {}
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            content()
        }
    }
}

/**
 * Component for bottom bar with centered floating actions
 */
@Composable
private fun BottomActionBar(
    actions: List<BottomBarAction>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            actions.forEachIndexed { index, action ->
                FloatingActionButton(
                    onClick = action.onClick,
                    modifier = Modifier.size(56.dp),
                    containerColor = if (index == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    contentColor = if (index == 0) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onError
                ) {
                    Icon(
                        imageVector = action.icon,
                        contentDescription = action.contentDescription,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppScaffoldUserListPreview() {
    DummyTheme {
        AppScaffold(
            screenType = ScreenType.USER_LIST,
            title = "User List",
            onAddUserClick = {}
        ) {
            Text("User List Content")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppScaffoldUserDetailPreview() {
    DummyTheme {
        AppScaffold(
            screenType = ScreenType.USER_DETAIL,
            title = "User Detail",
            onBackClick = {},
            onEditClick = {},
            onDeleteClick = {}
        ) {
            Text("User Detail Content")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppScaffoldCreateUserPreview() {
    DummyTheme {
        AppScaffold(
            screenType = ScreenType.CREATE_USER,
            title = "Create User",
            onBackClick = {},
            onSaveClick = {},
            onDiscardClick = {}
        ) {
            Text("Create User Content")
        }
    }
}
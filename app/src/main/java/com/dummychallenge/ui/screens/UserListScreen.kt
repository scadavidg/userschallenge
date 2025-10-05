package com.dummychallenge.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.dummychallenge.ui.components.AppScaffold
import com.dummychallenge.ui.components.DeleteConfirmationDialog
import com.dummychallenge.ui.components.ScreenType
import com.dummychallenge.ui.components.UserListPagingComponent
import com.dummychallenge.ui.navigation.Screen
import com.dummychallenge.viewmodel.UserListScreenViewModel

/**
 * Main screen that displays a list of users with pagination.
 * Uses collectAsState to observe UI state changes from the ViewModel.
 * hiltViewModel() provides dependency injection for the ViewModel.
 */
@Composable
fun UserListScreen(
    navController: NavController,
    viewModel: UserListScreenViewModel = hiltViewModel()
) {
    // collectAsState converts StateFlow to State for UI consumption
    val uiState by viewModel.uiState.collectAsState()

    // Refresh list when returning from other screens (e.g., after deleting a user)
    val backStackEntry by navController.currentBackStackEntryAsState()
    LaunchedEffect(backStackEntry?.destination?.route) {
        // Only refresh when we're actually on the user list screen and it's a navigation change
        if (backStackEntry?.destination?.route == "userList") {
            // Add a small delay to ensure the screen is fully loaded
            kotlinx.coroutines.delay(100)
            viewModel.refresh()
        }
    }

    AppScaffold(
        screenType = ScreenType.USER_LIST,
        title = "User List",
        onAddUserClick = {
            navController.navigate(Screen.CreateUser.route)
        }
    ) {
        UserListPagingComponent(
            users = uiState.users,
            isLoading = uiState.isLoading,
            isLoadingMore = uiState.isLoadingMore,
            hasMorePages = uiState.hasMorePages,
            error = uiState.error,
            onUserClick = { userId ->
                navController.navigate(Screen.UserDetail.createRoute(userId))
            },
            onEditClick = { userId ->
                navController.navigate(Screen.EditUser.createRoute(userId))
            },
            onDeleteClick = { userId ->
                viewModel.showDeleteDialog(userId)
            },
            onLoadMore = {
                viewModel.loadMoreUsers()
            },
            onRetry = {
                viewModel.refresh()
            },
            onCreateUser = {
                navController.navigate(Screen.CreateUser.route)
            },
            onRefresh = {
                viewModel.refresh()
            }
        )

        // Delete confirmation dialog
        if (uiState.showDeleteDialog) {
            val userToDelete = uiState.users.find { it.id == uiState.userToDelete }
            val userName = "${userToDelete?.firstName} ${userToDelete?.lastName}"

            DeleteConfirmationDialog(
                isVisible = uiState.showDeleteDialog,
                userName = userName,
                onConfirm = {
                    viewModel.deleteUser()
                },
                onDismiss = {
                    viewModel.hideDeleteDialog()
                }
            )
        }
    }
}
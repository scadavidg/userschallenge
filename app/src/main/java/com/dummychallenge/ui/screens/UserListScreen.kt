package com.dummychallenge.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("createUser")
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add User"
                )
            }
        }
    ) { paddingValues ->
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
            onDeleteClick = { _ ->
                // TODO: Implement delete functionality
            },
            onLoadMore = {
                viewModel.loadMoreUsers()
            },
            onRetry = {
                viewModel.refresh()
            },
            onCreateUser = {
                navController.navigate("createUser")
            },
            modifier = Modifier.padding(paddingValues)
        )
    }
}
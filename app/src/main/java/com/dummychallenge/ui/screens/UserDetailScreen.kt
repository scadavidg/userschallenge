package com.dummychallenge.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dummychallenge.ui.components.AppScaffold
import com.dummychallenge.ui.components.DeleteConfirmationDialog
import com.dummychallenge.ui.components.ErrorState
import com.dummychallenge.ui.components.ProfileImage
import com.dummychallenge.ui.components.ScreenType
import com.dummychallenge.ui.navigation.Screen
import com.dummychallenge.viewmodel.UserDetailScreenViewModel

@Composable
fun UserDetailScreen(
    navController: NavController,
    userId: String,
    viewModel: UserDetailScreenViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Load user data when screen initializes - using LaunchedEffect to avoid multiple loads
    LaunchedEffect(userId) {
        viewModel.loadUserDetail(userId)
    }

    // Navigate when user is deleted successfully
    LaunchedEffect(uiState.userDeleted) {
        if (uiState.userDeleted) {
            navController.navigate("userList") {
                popUpTo("userList") { inclusive = true }
                launchSingleTop = true
            }
        }
    }


    AppScaffold(
        screenType = ScreenType.USER_DETAIL,
        title = "User Detail",
        onBackClick = {
            navController.popBackStack()
        },
        onEditClick = {
            uiState.userDetail?.let { user ->
                navController.navigate(Screen.EditUser.createRoute(user.id))
            }
        },
        onDeleteClick = {
            viewModel.showDeleteDialog()
        }
    ) {
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null -> {
                ErrorState(
                    message = uiState.error ?: "Unknown error",
                    onRetry = {
                        viewModel.loadUserDetail(userId)
                    }
                )
            }

            uiState.userDetail != null -> {
                uiState.userDetail?.let { user ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Profile photo
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            ProfileImage(
                                imageUrl = user.picture,
                                contentDescription = "Profile photo",
                                modifier = Modifier.size(144.dp) // 20% bigger than 120dp
                            )
                        }

                        // Basic information - Title, First Name, Last Name in one row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${user.title} ${user.firstName} ${user.lastName}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }


                        // Contact information
                        Text(
                            text = "Contact Information",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Email: ${user.email}",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Text(
                            text = "Teléfono: ${user.phone}",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        // Personal information
                        Text(
                            text = "Personal Information",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Gender: ${user.gender}",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Text(
                            text = "Date of Birth: ${user.dateOfBirth}",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        // Location
                        user.location?.let { location ->
                            Text(
                                text = "Location",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "${location.street}, ${location.city}",
                                style = MaterialTheme.typography.bodyLarge
                            )

                            Text(
                                text = "${location.state}, ${location.country}",
                                style = MaterialTheme.typography.bodyLarge
                            )

                            Text(
                                text = "Timezone: ${location.timezone}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Dates
                        Text(
                            text = "Dates",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Registered: ${user.registerDate}",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Text(
                            text = "Updated: ${user.updatedDate}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }

        // Delete confirmation dialog
        uiState.userDetail?.let { user ->
            val userName = "${user.firstName} ${user.lastName}"

            DeleteConfirmationDialog(
                isVisible = uiState.showDeleteDialog,
                userName = userName,
                onConfirm = {
                    viewModel.deleteUser(userId)
                },
                onDismiss = {
                    viewModel.hideDeleteDialog()
                }
            )
        }
    }
}
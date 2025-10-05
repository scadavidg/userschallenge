package com.dummychallenge.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

/**
 * Reusable confirmation dialog for delete operations
 * 
 * @param isVisible Whether the dialog should be visible
 * @param userName The name of the user to be deleted (e.g., "John Doe")
 * @param onConfirm Callback when user confirms the deletion
 * @param onDismiss Callback when user dismisses the dialog
 */
@Composable
fun DeleteConfirmationDialog(
    isVisible: Boolean,
    userName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (isVisible) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text("Delete User")
            },
            text = {
                Text("Are you sure you want to delete $userName? This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = onConfirm
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismiss
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

package com.dummychallenge.ui.screens

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.dummychallenge.viewmodel.EditUserScreenViewModel

@Composable
fun EditUserScreen(
    navController: NavHostController,
    userId: String,
    viewModel: EditUserScreenViewModel = hiltViewModel()
) {
}
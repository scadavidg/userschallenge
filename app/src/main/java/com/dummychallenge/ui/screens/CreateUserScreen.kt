package com.dummychallenge.ui.screens

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.dummychallenge.viewmodel.CreateUserScreenViewModel

@Composable
fun CreateUserScreen(
    navController: NavHostController,
    viewModel: CreateUserScreenViewModel = hiltViewModel()
) {
}
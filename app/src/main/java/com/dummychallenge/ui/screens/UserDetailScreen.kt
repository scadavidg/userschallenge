package com.dummychallenge.ui.screens

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dummychallenge.viewmodel.UserDetailScreenViewModel

@Composable
fun UserDetailScreen(
    navController: NavController,
    userId: String,
    viewModel: UserDetailScreenViewModel = hiltViewModel()
){

}
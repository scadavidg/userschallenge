package com.dummychallenge.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dummychallenge.ui.screens.CreateUserScreen
import com.dummychallenge.ui.screens.EditUserScreen
import com.dummychallenge.ui.screens.UserDetailScreen
import com.dummychallenge.ui.screens.UserListScreen

/**
 * Navigation composable that defines the app's navigation graph.
 * Each composable represents a screen with its route and arguments.
 */
@Composable
fun Navigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.UserList.route
) {
    NavHost(
        navController = navController, startDestination = startDestination
    ) {
        composable(Screen.UserList.route) {
            UserListScreen(navController)
        }
        // Screens with parameters use navArgument to define argument types
        composable(
            Screen.UserDetail.route, arguments = listOf(
                navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            // Extract parameters from navigation arguments
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            UserDetailScreen(navController, userId)
        }
        composable(Screen.CreateUser.route) {
            CreateUserScreen(navController)
        }
        composable(
            Screen.EditUser.route,
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            EditUserScreen(navController, userId)
        }
    }
}
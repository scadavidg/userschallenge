package com.dummychallenge.ui.navigation

/**
 * Sealed class defining all app screens with their routes.
 * Sealed classes ensure type safety and exhaustive when expressions.
 * Helper functions create properly formatted routes with parameters.
 */
sealed class Screen(val route: String) {
    object UserList : Screen("userList")
    object UserDetail : Screen("userDetail/{userId}") {
        fun createRoute(userId: String) = "userDetail/$userId"
    }
    object CreateUser : Screen("createUser")
    object EditUser : Screen("editUser/{userId}") {
        fun createRoute(userId: String) = "editUser/$userId"
    }
}

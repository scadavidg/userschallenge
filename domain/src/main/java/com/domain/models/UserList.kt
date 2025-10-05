package com.domain.models

/**
 * Domain model representing a paginated list of users.
 * Contains pagination metadata and computed property for checking if more pages exist.
 */
data class UserList(
    val data: List<UserPreview>,
    val page: Int,
    val limit: Int,
    val total: Int
) {
    // Computed property that calculates if there are more pages available
    val hasMorePages: Boolean
        get() = (page + 1) * limit < total
}

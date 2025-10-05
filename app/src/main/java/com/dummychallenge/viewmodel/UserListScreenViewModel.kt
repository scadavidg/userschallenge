package com.dummychallenge.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domain.models.Result
import com.domain.models.UserPreview
import com.domain.usecases.DeleteUserUseCase
import com.domain.usecases.GetAllUserUseCase
import com.dummychallenge.utils.ErrorHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserListScreenViewModel @Inject constructor(
    private val getAllUserUseCase: GetAllUserUseCase,
    private val deleteUserUseCase: DeleteUserUseCase
) : ViewModel() {

    // Private mutable state for internal updates
    private val _uiState = MutableStateFlow(UserListUiState())
    // Public read-only state for UI consumption
    val uiState: StateFlow<UserListUiState> = _uiState.asStateFlow()

    // Pagination cache to avoid refetching already loaded pages
    private val cachedPages = mutableMapOf<Int, List<UserPreview>>()
    private var currentPage = 0
    private var totalPages = 0
    private var isLoadingMore = false
    
    // Track locally deleted users to filter them from API responses
    private val locallyDeletedUsers = mutableSetOf<String>()

    init {
        loadInitialData()
    }
    
    // Helper function to filter out locally deleted users
    private fun filterDeletedUsers(users: List<UserPreview>): List<UserPreview> {
        return users.filter { user -> !locallyDeletedUsers.contains(user.id) }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            when (val result = getAllUserUseCase(0)) {
                is Result.Success -> {
                    val userList = result.data
                    val filteredUsers = filterDeletedUsers(userList.data)
                    cachedPages[0] = filteredUsers
                    currentPage = 0
                    totalPages = (userList.total / userList.limit) + if (userList.total % userList.limit > 0) 1 else 0
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        users = filteredUsers,
                        hasMorePages = userList.hasMorePages,
                        error = null
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = ErrorHandler.getErrorMessage(result.message)
                    )
                }
                is Result.Loading -> {

                }
            }
        }
    }

    fun loadMoreUsers() {
        if (isLoadingMore || !_uiState.value.hasMorePages) return
        
        isLoadingMore = true
        _uiState.value = _uiState.value.copy(isLoadingMore = true)
        
        viewModelScope.launch {
            val nextPage = currentPage + 1
            
            // Check cache first to avoid unnecessary network calls
            if (cachedPages.containsKey(nextPage)) {
                val cachedUsers = cachedPages[nextPage]!!
                val filteredCachedUsers = filterDeletedUsers(cachedUsers)
                val currentUsers = _uiState.value.users
                _uiState.value = _uiState.value.copy(
                    users = currentUsers + filteredCachedUsers,
                    isLoadingMore = false,
                    hasMorePages = nextPage + 1 < totalPages
                )
                currentPage = nextPage
                isLoadingMore = false
                return@launch
            }
            
            when (val result = getAllUserUseCase(nextPage)) {
                is Result.Success -> {
                    val userList = result.data
                    val filteredUsers = filterDeletedUsers(userList.data)
                    cachedPages[nextPage] = filteredUsers
                    
                    val currentUsers = _uiState.value.users
                    _uiState.value = _uiState.value.copy(
                        users = currentUsers + filteredUsers,
                        isLoadingMore = false,
                        hasMorePages = userList.hasMorePages
                    )
                    currentPage = nextPage
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoadingMore = false,
                        error = result.message
                    )
                }
                is Result.Loading -> {

                }
            }
            
            isLoadingMore = false
        }
    }

    fun refresh() {
        cachedPages.clear()
        locallyDeletedUsers.clear() // Clear locally deleted users on refresh
        currentPage = 0
        loadInitialData()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun showDeleteDialog(userId: String) {
        _uiState.value = _uiState.value.copy(
            showDeleteDialog = true,
            userToDelete = userId
        )
    }

    fun hideDeleteDialog() {
        _uiState.value = _uiState.value.copy(
            showDeleteDialog = false,
            userToDelete = null
        )
    }

    fun deleteUser() {
        val userId = _uiState.value.userToDelete ?: return
        
        // Add to locally deleted users list
        locallyDeletedUsers.add(userId)
        
        // Immediately remove user from UI for better UX
        val currentUsers = _uiState.value.users
        val updatedUsers = currentUsers.filter { it.id != userId }
        
        _uiState.value = _uiState.value.copy(
            users = updatedUsers,
            showDeleteDialog = false,
            userToDelete = null
        )
        
        // Perform API call in background
        viewModelScope.launch {
            when (val result = deleteUserUseCase(userId)) {
                is Result.Success -> {
                    // Remove user from cached pages to ensure consistency
                    cachedPages.forEach { (pageNumber, users) ->
                        cachedPages[pageNumber] = users.filter { it.id != userId }
                    }
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = null
                    )
                }
                is Result.Error -> {
                    // If API call fails, restore the user to the list
                    locallyDeletedUsers.remove(userId) // Remove from deleted list
                    _uiState.value = _uiState.value.copy(
                        users = currentUsers, // Restore original list
                        isLoading = false,
                        error = ErrorHandler.getErrorMessage(result.message)
                    )
                }
                is Result.Loading -> {
                    // Loading state is already handled above
                }
            }
        }
    }
}

/**
 * UI state data class that represents all possible states of the user list screen.
 * This pattern is called "UI State" and helps manage complex UI states in a type-safe way.
 * All properties have default values to ensure the UI is always in a valid state.
 */
data class UserListUiState(
    val users: List<UserPreview> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMorePages: Boolean = true,
    val error: String? = null,
    val showDeleteDialog: Boolean = false,
    val userToDelete: String? = null,
    val updateTrigger: Long = System.currentTimeMillis() // Force recomposition when users change
)

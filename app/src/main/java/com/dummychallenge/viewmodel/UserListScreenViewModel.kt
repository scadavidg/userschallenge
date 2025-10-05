package com.dummychallenge.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domain.models.Result
import com.domain.models.UserPreview
import com.domain.usecases.GetAllUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserListScreenViewModel @Inject constructor(
    private val getAllUserUseCase: GetAllUserUseCase
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

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            when (val result = getAllUserUseCase(0)) {
                is Result.Success -> {
                    val userList = result.data
                    cachedPages[0] = userList.data
                    currentPage = 0
                    totalPages = (userList.total / userList.limit) + if (userList.total % userList.limit > 0) 1 else 0
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        users = userList.data,
                        hasMorePages = userList.hasMorePages,
                        error = null
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
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
                val currentUsers = _uiState.value.users
                _uiState.value = _uiState.value.copy(
                    users = currentUsers + cachedUsers,
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
                    cachedPages[nextPage] = userList.data
                    
                    val currentUsers = _uiState.value.users
                    _uiState.value = _uiState.value.copy(
                        users = currentUsers + userList.data,
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
        currentPage = 0
        loadInitialData()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
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
    val error: String? = null
)

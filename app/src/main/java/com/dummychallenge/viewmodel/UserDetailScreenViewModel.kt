package com.dummychallenge.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domain.models.Result
import com.domain.models.UserDetail
import com.domain.usecases.DeleteUserUseCase
import com.domain.usecases.GetUserDetailUseCase
import com.dummychallenge.utils.CrashlyticsLogger
import com.dummychallenge.utils.ErrorHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserDetailScreenViewModel @Inject constructor(
    private val getUserDetailUseCase: GetUserDetailUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
    val crashlyticsLogger: CrashlyticsLogger,
    private val errorHandler: ErrorHandler
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserDetailUiState())
    val uiState: StateFlow<UserDetailUiState> = _uiState.asStateFlow()

    fun loadUserDetail(userId: String) {
        crashlyticsLogger.log("Loading user details")
        crashlyticsLogger.setCustomKey("detail_user_id", userId)
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, userDeleted = false)
            val startTime = System.currentTimeMillis()
            
            when (val result = getUserDetailUseCase(userId)) {
                is Result.Success -> {
                    val duration = System.currentTimeMillis() - startTime
                    crashlyticsLogger.log("User details loaded successfully")
                    crashlyticsLogger.logPerformance("loadUserDetail", duration, true)
                    crashlyticsLogger.setCustomKey("loaded_user_email", result.data.email)
                    crashlyticsLogger.setCustomKey("loaded_user_name", "${result.data.firstName} ${result.data.lastName}")
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        userDetail = result.data,
                        error = null
                    )
                }
                is Result.Error -> {
                    val duration = System.currentTimeMillis() - startTime
                    crashlyticsLogger.logNetworkError("getUserDetail", result.message)
                    crashlyticsLogger.logPerformance("loadUserDetail", duration, false)
                    crashlyticsLogger.logError(Exception(result.message), "Failed to load user details")
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = errorHandler.getErrorMessage(result.message)
                    )
                }
                is Result.Loading -> {
                    // Loading state is already set above
                }
            }
        }
    }

    fun showDeleteDialog() {
        crashlyticsLogger.log("Delete dialog shown")
        _uiState.value = _uiState.value.copy(showDeleteDialog = true)
    }

    fun hideDeleteDialog() {
        crashlyticsLogger.log("Delete dialog hidden")
        _uiState.value = _uiState.value.copy(showDeleteDialog = false)
    }

    fun deleteUser(userId: String) {
        crashlyticsLogger.log("User deletion initiated from detail screen")
        crashlyticsLogger.setCustomKey("delete_user_id", userId)
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val startTime = System.currentTimeMillis()
            
            when (val result = deleteUserUseCase(userId)) {
                is Result.Success -> {
                    val duration = System.currentTimeMillis() - startTime
                    crashlyticsLogger.log("User deleted successfully from detail screen")
                    crashlyticsLogger.logPerformance("deleteUser", duration, true)
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        showDeleteDialog = false,
                        error = null,
                        userDeleted = true
                    )
                }
                is Result.Error -> {
                    val duration = System.currentTimeMillis() - startTime
                    crashlyticsLogger.logNetworkError("deleteUser", result.message)
                    crashlyticsLogger.logPerformance("deleteUser", duration, false)
                    crashlyticsLogger.logError(Exception(result.message), "Failed to delete user from detail screen")
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        showDeleteDialog = false,
                        error = errorHandler.getErrorMessage(result.message)
                    )
                }
                is Result.Loading -> {
                    // Loading state is already set above
                }
            }
        }
    }
}

data class UserDetailUiState(
    val userDetail: UserDetail? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showDeleteDialog: Boolean = false,
    val userDeleted: Boolean = false
)
package com.dummychallenge.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domain.models.Result
import com.domain.models.UserDetail
import com.domain.usecases.DeleteUserUseCase
import com.domain.usecases.GetUserDetailUseCase
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
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserDetailUiState())
    val uiState: StateFlow<UserDetailUiState> = _uiState.asStateFlow()

    fun loadUserDetail(userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, userDeleted = false)
            
            when (val result = getUserDetailUseCase(userId)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        userDetail = result.data,
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
                    // Loading state is already set above
                }
            }
        }
    }

    fun showDeleteDialog() {
        _uiState.value = _uiState.value.copy(showDeleteDialog = true)
    }

    fun hideDeleteDialog() {
        _uiState.value = _uiState.value.copy(showDeleteDialog = false)
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            when (val result = deleteUserUseCase(userId)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        showDeleteDialog = false,
                        error = null,
                        userDeleted = true
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        showDeleteDialog = false,
                        error = ErrorHandler.getErrorMessage(result.message)
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
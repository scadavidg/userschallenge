package com.dummychallenge.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domain.models.Location
import com.domain.models.Result
import com.domain.models.UserDetail
import com.domain.usecases.GetUserDetailUseCase
import com.domain.usecases.UpdateUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditUserScreenViewModel @Inject constructor(
    private val getUserDetailUseCase: GetUserDetailUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditUserUiState())
    val uiState: StateFlow<EditUserUiState> = _uiState.asStateFlow()

    fun loadUserDetail(userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
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
                        error = result.message
                    )
                }
                is Result.Loading -> {
                    // Loading state is already set above
                }
            }
        }
    }

    fun updateUser(
        userId: String,
        firstName: String,
        lastName: String,
        email: String,
        phone: String
    ) {
        if (firstName.isBlank() || lastName.isBlank() || email.isBlank()) {
            _uiState.value = _uiState.value.copy(
                error = "First name, last name and email are required"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            val currentUser = _uiState.value.userDetail
            if (currentUser == null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Could not load user information"
                )
                return@launch
            }
            
            val updatedUser = currentUser.copy(
                firstName = firstName,
                lastName = lastName,
                email = email,
                phone = phone,
                updatedDate = java.time.LocalDateTime.now().toString()
            )
            
            when (val result = updateUserUseCase(updatedUser)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        userDetail = result.data,
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
                    // Loading state is already set above
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class EditUserUiState(
    val userDetail: UserDetail? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)
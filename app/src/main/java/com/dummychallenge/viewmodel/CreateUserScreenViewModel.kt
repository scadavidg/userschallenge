package com.dummychallenge.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domain.models.Location
import com.domain.models.Result
import com.domain.models.UserDetail
import com.domain.usecases.CreateUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CreateUserScreenViewModel @Inject constructor(
    private val createUserUseCase: CreateUserUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateUserUiState())
    val uiState: StateFlow<CreateUserUiState> = _uiState.asStateFlow()

    fun createUser(
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
            
            val userDetail = UserDetail(
                id = UUID.randomUUID().toString(),
                title = "mr", // Default title
                firstName = firstName,
                lastName = lastName,
                picture = "https://randomuser.me/api/portraits/men/1.jpg", // Default picture
                gender = "male", // Default gender
                email = email,
                dateOfBirth = "1990-01-01", // Default date
                phone = phone,
                location = Location(
                    street = "123 Main St",
                    city = "New York",
                    state = "NY",
                    country = "USA",
                    timezone = "-5:00"
                ),
                registerDate = java.time.LocalDateTime.now().toString(),
                updatedDate = java.time.LocalDateTime.now().toString()
            )
            
            when (val result = createUserUseCase(userDetail)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true,
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

data class CreateUserUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)
package com.dummychallenge.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domain.models.Result
import com.domain.models.UserDetail
import com.domain.usecases.GetUserDetailUseCase
import com.domain.usecases.UpdateUserUseCase
import com.dummychallenge.utils.CrashlyticsLogger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class EditUserScreenViewModel @Inject constructor(
    private val getUserDetailUseCase: GetUserDetailUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    val crashlyticsLogger: CrashlyticsLogger
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditUserUiState())
    val uiState: StateFlow<EditUserUiState> = _uiState.asStateFlow()

    fun loadUserDetail(userId: String) {
        crashlyticsLogger.log("Loading user details for editing")
        crashlyticsLogger.setCustomKey("edit_user_id", userId)
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val startTime = System.currentTimeMillis()
            
            when (val result = getUserDetailUseCase(userId)) {
                is Result.Success -> {
                    val duration = System.currentTimeMillis() - startTime
                    crashlyticsLogger.log("User details loaded successfully for editing")
                    crashlyticsLogger.logPerformance("loadUserDetail", duration, true)
                    crashlyticsLogger.setCustomKey("loaded_user_email", result.data.email)
                    
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
                    crashlyticsLogger.logError(Exception(result.message), "Failed to load user details for editing")
                    
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
        title: String,
        firstName: String,
        lastName: String,
        gender: String,
        email: String,
        dateOfBirth: String,
        phone: String,
        picture: String
    ) {
        crashlyticsLogger.log("User update initiated")
        crashlyticsLogger.setCustomKey("update_user_id", userId)
        crashlyticsLogger.setCustomKey("update_user_title", title)
        crashlyticsLogger.setCustomKey("update_user_gender", gender)
        
        val validationError = validateUserData(
            title, firstName, lastName, gender, email, dateOfBirth, phone
        )

        if (validationError != null) {
            crashlyticsLogger.logFormValidationError("EditUser", "validation", validationError)
            _uiState.value = _uiState.value.copy(error = validationError)
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val startTime = System.currentTimeMillis()
            
            val currentUser = _uiState.value.userDetail
            if (currentUser == null) {
                crashlyticsLogger.logError(Exception("User not loaded"), "Cannot update user - user details not loaded")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Could not load user information"
                )
                return@launch
            }
            
            val updatedUser = currentUser.copy(
                title = title.lowercase(),
                firstName = firstName,
                lastName = lastName,
                gender = gender.lowercase(),
                email = email,
                dateOfBirth = formatDateToISO(dateOfBirth),
                phone = phone,
                picture = picture,
                updatedDate = java.time.LocalDateTime.now().toString()
            )
            
            when (val result = updateUserUseCase(updatedUser)) {
                is Result.Success -> {
                    val duration = System.currentTimeMillis() - startTime
                    crashlyticsLogger.log("User updated successfully")
                    crashlyticsLogger.logPerformance("updateUser", duration, true)
                    crashlyticsLogger.setCustomKey("updated_user_id", result.data.id)
                    crashlyticsLogger.setCustomKey("updated_user_email", result.data.email)
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        userDetail = result.data,
                        error = null
                    )
                }
                is Result.Error -> {
                    val duration = System.currentTimeMillis() - startTime
                    crashlyticsLogger.logNetworkError("updateUser", result.message)
                    crashlyticsLogger.logPerformance("updateUser", duration, false)
                    crashlyticsLogger.logError(Exception(result.message), "Failed to update user")
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = parseServerError(result.message)
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

    private fun validateUserData(
        title: String,
        firstName: String,
        lastName: String,
        gender: String,
        email: String,
        dateOfBirth: String,
        phone: String
    ): String? {
        return when {
            title.isBlank() -> "Title is required"
            !listOf("mr", "ms", "mrs", "miss").contains(title.lowercase()) -> "Please select a valid title"
            firstName.isBlank() -> "First name is required"
            firstName.length < 2 -> "First name must be at least 2 characters"
            lastName.isBlank() -> "Last name is required"
            lastName.length < 2 -> "Last name must be at least 2 characters"
            gender.isBlank() -> "Gender is required"
            !listOf("male", "female").contains(gender.lowercase()) -> "Please select a valid gender"
            email.isBlank() -> "Email is required"
            !isValidEmail(email) -> "Please enter a valid email address"
            dateOfBirth.isBlank() -> "Date of birth is required"
            !isValidDateFormat(dateOfBirth) -> "Please enter a valid date in YYYY-MM-DD format"
            phone.isBlank() -> "Phone is required"
            !phone.matches(Regex("\\d{10,}")) -> "Phone must be numeric with at least 10 digits"
            else -> null
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidDateFormat(dateString: String): Boolean {
        return try {
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            formatter.isLenient = false
            formatter.parse(dateString)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun formatDateToISO(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            outputFormat.format(date ?: java.util.Date())
        } catch (e: Exception) {
            java.time.LocalDateTime.now().toString()
        }
    }

    private fun parseServerError(errorMessage: String): String {
        return try {
            crashlyticsLogger.log("Parsing server error for user update: $errorMessage")
            
            if (errorMessage.contains("BODY_NOT_VALID")) {
                if (errorMessage.contains("email")) {
                    crashlyticsLogger.setCustomKey("update_server_error_type", "email_exists")
                    "Email already exists. Please use a different email address."
                } else if (errorMessage.contains("gender")) {
                    crashlyticsLogger.setCustomKey("update_server_error_type", "invalid_gender")
                    "Invalid gender value. Please select a valid gender."
                } else if (errorMessage.contains("lastName")) {
                    crashlyticsLogger.setCustomKey("update_server_error_type", "missing_lastname")
                    "Last name is required."
                } else {
                    crashlyticsLogger.setCustomKey("update_server_error_type", "invalid_data")
                    "Invalid data provided. Please check your input and try again."
                }
            } else {
                crashlyticsLogger.setCustomKey("update_server_error_type", "unknown")
                errorMessage
            }
        } catch (e: Exception) {
            crashlyticsLogger.logError(e, "Error parsing server error message for user update")
            "An error occurred while updating the user. Please try again."
        }
    }
}

data class EditUserUiState(
    val userDetail: UserDetail? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)
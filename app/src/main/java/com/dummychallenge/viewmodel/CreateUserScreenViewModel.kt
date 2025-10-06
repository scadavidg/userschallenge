package com.dummychallenge.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domain.models.Result
import com.domain.models.UserDetail
import com.domain.usecases.CreateUserUseCase
import com.dummychallenge.utils.CrashlyticsLogger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CreateUserScreenViewModel @Inject constructor(
    private val createUserUseCase: CreateUserUseCase,
    val crashlyticsLogger: CrashlyticsLogger
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateUserUiState())
    val uiState: StateFlow<CreateUserUiState> = _uiState.asStateFlow()

    fun createUser(
        title: String,
        firstName: String,
        lastName: String,
        gender: String,
        email: String,
        dateOfBirth: String,
        phone: String,
        picture: String
    ) {
        crashlyticsLogger.log("User creation initiated")
        crashlyticsLogger.setCustomKey("user_creation_title", title)
        crashlyticsLogger.setCustomKey("user_creation_gender", gender)
        
        // Validate all required fields
        val validationError = validateUserData(
            title, firstName, lastName, gender, email, dateOfBirth, phone
        )

        if (validationError != null) {
            crashlyticsLogger.logFormValidationError("CreateUser", "validation", validationError)
            _uiState.value = _uiState.value.copy(error = validationError)
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val startTime = System.currentTimeMillis()

            val userDetail = UserDetail(
                id = UUID.randomUUID().toString(),
                title = title.lowercase(),
                firstName = firstName,
                lastName = lastName,
                picture = picture,
                gender = gender.lowercase(),
                email = email,
                dateOfBirth = formatDateToISO(dateOfBirth),
                phone = phone,
                country = null, // No country field
                location = null,
                registerDate = java.time.LocalDateTime.now().toString(),
                updatedDate = java.time.LocalDateTime.now().toString()
            )
            val result = createUserUseCase(userDetail)
            val duration = System.currentTimeMillis() - startTime
            
            when (result) {
                is Result.Success -> {
                    crashlyticsLogger.log("User created successfully")
                    crashlyticsLogger.logPerformance("createUser", duration, true)
                    crashlyticsLogger.setCustomKey("created_user_id", result.data.id)
                    crashlyticsLogger.setCustomKey("created_user_email", result.data.email)
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        error = null
                    )
                }

                is Result.Error -> {
                    crashlyticsLogger.logNetworkError("createUser", result.message)
                    crashlyticsLogger.logPerformance("createUser", duration, false)
                    crashlyticsLogger.logError(Exception(result.message), "Failed to create user")
                    
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
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            java.time.LocalDateTime.now().toString()
        }
    }

    private fun parseServerError(errorMessage: String): String {
        return try {
            crashlyticsLogger.log("Parsing server error: $errorMessage")
            
            // Parse server error response
            if (errorMessage.contains("BODY_NOT_VALID")) {
                if (errorMessage.contains("email")) {
                    crashlyticsLogger.setCustomKey("server_error_type", "email_exists")
                    "Email already exists. Please use a different email address."
                } else if (errorMessage.contains("gender")) {
                    crashlyticsLogger.setCustomKey("server_error_type", "invalid_gender")
                    "Invalid gender value. Please select a valid gender."
                } else if (errorMessage.contains("lastName")) {
                    crashlyticsLogger.setCustomKey("server_error_type", "missing_lastname")
                    "Last name is required."
                } else {
                    crashlyticsLogger.setCustomKey("server_error_type", "invalid_data")
                    "Invalid data provided. Please check your input and try again."
                }
            } else {
                crashlyticsLogger.setCustomKey("server_error_type", "unknown")
                errorMessage
            }
        } catch (e: Exception) {
            crashlyticsLogger.logError(e, "Error parsing server error message")
            "An error occurred while creating the user. Please try again."
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
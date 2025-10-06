package com.dummychallenge.viewmodel

import com.domain.models.Result
import com.domain.models.UserDetail
import com.domain.usecases.CreateUserUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource

@OptIn(ExperimentalCoroutinesApi::class)
class CreateUserScreenViewModelTest {

    private val createUserUseCase: CreateUserUseCase = mockk()
    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var viewModel: CreateUserScreenViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = CreateUserScreenViewModel(createUserUseCase)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Nested
    @DisplayName("Given initial state")
    inner class InitialStateTests {

        @Test
        @DisplayName("When viewModel is created, then should have initial state")
        fun `Given viewModel creation, When initialized, Then should have initial state`() = runTest {
            // Given & When
            val uiState = viewModel.uiState.value

            // Then
            assert(!uiState.isLoading)
            assert(!uiState.isSuccess)
            assert(uiState.error == null)
        }
    }

    @Nested
    @DisplayName("Given valid user data")
    inner class ValidUserDataTests {

        private val validUserData = mapOf(
            "title" to "Mr",
            "firstName" to "John",
            "lastName" to "Doe",
            "gender" to "Male",
            "email" to "john.doe@example.com",
            "dateOfBirth" to "1990-01-15",
            "phone" to "1234567890",
            "picture" to "https://example.com/picture.jpg"
        )

        @Test
        @DisplayName("When createUser is called with valid data, then should create user successfully")
        fun `Given valid user data, When createUser called, Then should create user successfully`() = runTest {
            // Given
            val mockUserDetail = UserDetail(
                id = "123",
                title = "mr",
                firstName = "John",
                lastName = "Doe",
                picture = "https://example.com/picture.jpg",
                gender = "male",
                email = "john.doe@example.com",
                dateOfBirth = "1990-01-15T00:00:00.000Z",
                phone = "1234567890",
                country = null,
                location = null,
                registerDate = "2024-01-01T00:00:00.000Z",
                updatedDate = "2024-01-01T00:00:00.000Z"
            )
            coEvery { createUserUseCase(any()) } returns Result.Success(mockUserDetail)

            // When
            viewModel.createUser(
                title = validUserData["title"]!!,
                firstName = validUserData["firstName"]!!,
                lastName = validUserData["lastName"]!!,
                gender = validUserData["gender"]!!,
                email = validUserData["email"]!!,
                dateOfBirth = validUserData["dateOfBirth"]!!,
                phone = validUserData["phone"]!!,
                picture = validUserData["picture"]!!
            )
            advanceUntilIdle()

            // Then
            coVerify { createUserUseCase(any()) }
            assert(viewModel.uiState.value.isSuccess)
            assert(!viewModel.uiState.value.isLoading)
            assert(viewModel.uiState.value.error == null)
        }

        @Test
        @DisplayName("When createUser fails, then should show error state")
        fun `Given valid user data, When createUser fails, Then should show error state`() = runTest {
            // Given
            val errorMessage = "Email already exists"
            coEvery { createUserUseCase(any()) } returns Result.Error(errorMessage)

            // When
            viewModel.createUser(
                title = validUserData["title"]!!,
                firstName = validUserData["firstName"]!!,
                lastName = validUserData["lastName"]!!,
                gender = validUserData["gender"]!!,
                email = validUserData["email"]!!,
                dateOfBirth = validUserData["dateOfBirth"]!!,
                phone = validUserData["phone"]!!,
                picture = validUserData["picture"]!!
            )
            advanceUntilIdle()

            // Then
            assert(!viewModel.uiState.value.isSuccess)
            assert(!viewModel.uiState.value.isLoading)
            assert(viewModel.uiState.value.error == errorMessage)
        }
    }

    @Nested
    @DisplayName("Given invalid user data")
    inner class InvalidUserDataTests {

        @ParameterizedTest
        @CsvSource(
            "'', John, Doe, Male, john@example.com, 1990-01-15, 1234567890, 'Title is required'",
            "Mr, '', Doe, Male, john@example.com, 1990-01-15, 1234567890, 'First name is required'",
            "Mr, J, Doe, Male, john@example.com, 1990-01-15, 1234567890, 'First name must be at least 2 characters'",
            "Mr, John, '', Male, john@example.com, 1990-01-15, 1234567890, 'Last name is required'",
            "Mr, John, D, Male, john@example.com, 1990-01-15, 1234567890, 'Last name must be at least 2 characters'",
            "Mr, John, Doe, '', john@example.com, 1990-01-15, 1234567890, 'Gender is required'",
            "Mr, John, Doe, Male, '', 1990-01-15, 1234567890, 'Email is required'",
            "Mr, John, Doe, Male, invalid-email, 1990-01-15, 1234567890, 'Please enter a valid email address'",
            "Mr, John, Doe, Male, john@example.com, '', 1234567890, 'Date of birth is required'",
            "Mr, John, Doe, Male, john@example.com, invalid-date, 1234567890, 'Please enter a valid date in YYYY-MM-DD format'",
            "Mr, John, Doe, Male, john@example.com, 1990-01-15, '', 'Phone is required'",
            "Mr, John, Doe, Male, john@example.com, 1990-01-15, 123, 'Phone must be numeric with at least 10 digits'"
        )
        @DisplayName("When createUser is called with invalid data, then should return validation error")
        fun `Given invalid user data, When createUser called, Then should return validation error`(
            title: String,
            firstName: String,
            lastName: String,
            gender: String,
            email: String,
            dateOfBirth: String,
            phone: String,
            expectedError: String
        ) = runTest {
            // Given & When
            viewModel.createUser(
                title = title,
                firstName = firstName,
                lastName = lastName,
                gender = gender,
                email = email,
                dateOfBirth = dateOfBirth,
                phone = phone,
                picture = "https://example.com/picture.jpg"
            )

            // Then
            assert(viewModel.uiState.value.error == expectedError)
            assert(!viewModel.uiState.value.isSuccess)
            assert(!viewModel.uiState.value.isLoading)
        }

        @ParameterizedTest
        @ValueSource(strings = ["invalid", "INVALID", "Invalid"])
        @DisplayName("When title is invalid, then should return validation error")
        fun `Given invalid title, When createUser called, Then should return validation error`(invalidTitle: String) = runTest {
            // Given & When
            viewModel.createUser(
                title = invalidTitle,
                firstName = "John",
                lastName = "Doe",
                gender = "Male",
                email = "john@example.com",
                dateOfBirth = "1990-01-15",
                phone = "1234567890",
                picture = "https://example.com/picture.jpg"
            )

            // Then
            assert(viewModel.uiState.value.error == "Please select a valid title")
        }

        @ParameterizedTest
        @ValueSource(strings = ["invalid", "INVALID", "Invalid"])
        @DisplayName("When gender is invalid, then should return validation error")
        fun `Given invalid gender, When createUser called, Then should return validation error`(invalidGender: String) = runTest {
            // Given & When
            viewModel.createUser(
                title = "Mr",
                firstName = "John",
                lastName = "Doe",
                gender = invalidGender,
                email = "john@example.com",
                dateOfBirth = "1990-01-15",
                phone = "1234567890",
                picture = "https://example.com/picture.jpg"
            )

            // Then
            assert(viewModel.uiState.value.error == "Please select a valid gender")
        }
    }

    @Nested
    @DisplayName("Given server error handling")
    inner class ServerErrorHandlingTests {

        @Test
        @DisplayName("When server returns email already used error, then should parse and show specific message")
        fun `Given server error, When email already used, Then should show specific message`() = runTest {
            // Given
            val serverError = "BODY_NOT_VALID: email already used"
            coEvery { createUserUseCase(any()) } returns Result.Error(serverError)

            // When
            viewModel.createUser(
                title = "Mr",
                firstName = "John",
                lastName = "Doe",
                gender = "Male",
                email = "john@example.com",
                dateOfBirth = "1990-01-15",
                phone = "1234567890",
                picture = "https://example.com/picture.jpg"
            )
            advanceUntilIdle()

            // Then
            assert(viewModel.uiState.value.error == "Email already exists. Please use a different email address.")
        }

        @Test
        @DisplayName("When server returns gender error, then should parse and show specific message")
        fun `Given server error, When gender invalid, Then should show specific message`() = runTest {
            // Given
            val serverError = "BODY_NOT_VALID: gender is not a valid enum value"
            coEvery { createUserUseCase(any()) } returns Result.Error(serverError)

            // When
            viewModel.createUser(
                title = "Mr",
                firstName = "John",
                lastName = "Doe",
                gender = "Male",
                email = "john@example.com",
                dateOfBirth = "1990-01-15",
                phone = "1234567890",
                picture = "https://example.com/picture.jpg"
            )
            advanceUntilIdle()

            // Then
            assert(viewModel.uiState.value.error == "Invalid gender value. Please select a valid gender.")
        }

        @Test
        @DisplayName("When server returns lastName error, then should parse and show specific message")
        fun `Given server error, When lastName required, Then should show specific message`() = runTest {
            // Given
            val serverError = "BODY_NOT_VALID: lastName is required"
            coEvery { createUserUseCase(any()) } returns Result.Error(serverError)

            // When
            viewModel.createUser(
                title = "Mr",
                firstName = "John",
                lastName = "Doe",
                gender = "Male",
                email = "john@example.com",
                dateOfBirth = "1990-01-15",
                phone = "1234567890",
                picture = "https://example.com/picture.jpg"
            )
            advanceUntilIdle()

            // Then
            assert(viewModel.uiState.value.error == "Last name is required.")
        }
    }

    @Nested
    @DisplayName("Given error clearing")
    inner class ErrorClearingTests {

        @Test
        @DisplayName("When clearError is called, then should clear error state")
        fun `Given error state, When clearError called, Then should clear error state`() = runTest {
            // Given
            viewModel.createUser(
                title = "",
                firstName = "John",
                lastName = "Doe",
                gender = "Male",
                email = "john@example.com",
                dateOfBirth = "1990-01-15",
                phone = "1234567890",
                picture = "https://example.com/picture.jpg"
            )
            assert(viewModel.uiState.value.error != null)

            // When
            viewModel.clearError()

            // Then
            assert(viewModel.uiState.value.error == null)
        }
    }
}

package com.dummychallenge.viewmodel

import com.domain.models.Location
import com.domain.models.Result
import com.domain.models.UserDetail
import com.domain.usecases.GetUserDetailUseCase
import com.domain.usecases.UpdateUserUseCase
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
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

@OptIn(ExperimentalCoroutinesApi::class)
class EditUserScreenViewModelTest {

    private val getUserDetailUseCase: GetUserDetailUseCase = mockk()
    private val updateUserUseCase: UpdateUserUseCase = mockk()
    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var viewModel: EditUserScreenViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = EditUserScreenViewModel(getUserDetailUseCase, updateUserUseCase)
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
            assert(uiState.userDetail == null)
        }
    }

    @Nested
    @DisplayName("Given user detail loading")
    inner class UserDetailLoadingTests {

        @Test
        @DisplayName("When loadUserDetail is called successfully, then should load user data")
        fun `Given user ID, When loadUserDetail succeeds, Then should load user data`() = runTest {
            // Given
            val userId = "123"
            val mockUserDetail = UserDetail(
                id = userId,
                title = "mr",
                firstName = "John",
                lastName = "Doe",
                picture = "https://example.com/picture.jpg",
                gender = "male",
                email = "john.doe@example.com",
                dateOfBirth = "1990-01-15T00:00:00.000Z",
                phone = "1234567890",
                country = "USA",
                location = Location(
                    street = "123 Main St",
                    city = "New York",
                    state = "NY",
                    country = "USA",
                    timezone = "-5:00"
                ),
                registerDate = "2024-01-01T00:00:00.000Z",
                updatedDate = "2024-01-01T00:00:00.000Z"
            )
            coEvery { getUserDetailUseCase(userId) } returns Result.Success(mockUserDetail)

            // When
            viewModel.loadUserDetail(userId)
            advanceUntilIdle()

            // Then
            coVerify { getUserDetailUseCase(userId) }
            assert(viewModel.uiState.value.userDetail == mockUserDetail)
            assert(!viewModel.uiState.value.isLoading)
            assert(viewModel.uiState.value.error == null)
        }

        @Test
        @DisplayName("When loadUserDetail fails, then should show error state")
        fun `Given user ID, When loadUserDetail fails, Then should show error state`() = runTest {
            // Given
            val userId = "123"
            val errorMessage = "User not found"
            coEvery { getUserDetailUseCase(userId) } returns Result.Error(errorMessage)

            // When
            viewModel.loadUserDetail(userId)
            advanceUntilIdle()

            // Then
            assert(viewModel.uiState.value.error == errorMessage)
            assert(!viewModel.uiState.value.isLoading)
            assert(viewModel.uiState.value.userDetail == null)
        }
    }

    @Nested
    @DisplayName("Given user update")
    inner class UserUpdateTests {

        private val existingUser = UserDetail(
            id = "123",
            title = "mr",
            firstName = "John",
            lastName = "Doe",
            picture = "https://example.com/picture.jpg",
            gender = "male",
            email = "john.doe@example.com",
            dateOfBirth = "1990-01-15T00:00:00.000Z",
            phone = "1234567890",
            country = "USA",
            location = Location(
                street = "123 Main St",
                city = "New York",
                state = "NY",
                country = "USA",
                timezone = "-5:00"
            ),
            registerDate = "2024-01-01T00:00:00.000Z",
            updatedDate = "2024-01-01T00:00:00.000Z"
        )

        @BeforeEach
        fun setupWithLoadedUser() = runTest {
            coEvery { getUserDetailUseCase("123") } returns Result.Success(existingUser)
            viewModel.loadUserDetail("123")
            advanceUntilIdle()
        }

        @Test
        @DisplayName("When updateUser is called with valid data, then should update user successfully")
        fun `Given valid user data, When updateUser called, Then should update user successfully`() = runTest {
            // Given
            val updatedUser = existingUser.copy(
                firstName = "Jane",
                lastName = "Smith",
                email = "jane.smith@example.com"
            )
            coEvery { updateUserUseCase(any()) } returns Result.Success(updatedUser)

            // When
            viewModel.updateUser(
                userId = "123",
                title = "Ms",
                firstName = "Jane",
                lastName = "Smith",
                gender = "Female",
                email = "jane.smith@example.com",
                dateOfBirth = "1992-05-20",
                phone = "9876543210",
                picture = "https://example.com/new-picture.jpg"
            )
            advanceUntilIdle()

            // Then
            coVerify { updateUserUseCase(any()) }
            assert(viewModel.uiState.value.isSuccess)
            assert(!viewModel.uiState.value.isLoading)
            assert(viewModel.uiState.value.error == null)
        }

        @Test
        @DisplayName("When updateUser fails, then should show error state")
        fun `Given valid user data, When updateUser fails, Then should show error state`() = runTest {
            // Given
            val errorMessage = "Update failed"
            coEvery { updateUserUseCase(any()) } returns Result.Error(errorMessage)

            // When
            viewModel.updateUser(
                userId = "123",
                title = "Ms",
                firstName = "Jane",
                lastName = "Smith",
                gender = "Female",
                email = "jane.smith@example.com",
                dateOfBirth = "1992-05-20",
                phone = "9876543210",
                picture = "https://example.com/new-picture.jpg"
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
        @DisplayName("When updateUser is called with invalid data, then should return validation error")
        fun `Given invalid user data, When updateUser called, Then should return validation error`(
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
            viewModel.updateUser(
                userId = "123",
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
    }

    @Nested
    @DisplayName("Given server error handling")
    inner class ServerErrorHandlingTests {

        @Test
        @DisplayName("When server returns email already used error, then should parse and show specific message")
        fun `Given server error, When email already used, Then should show specific message`() = runTest {
            // Given
            val serverError = "BODY_NOT_VALID: email already used"
            coEvery { updateUserUseCase(any()) } returns Result.Error(serverError)

            // When
            viewModel.updateUser(
                userId = "123",
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
            coEvery { updateUserUseCase(any()) } returns Result.Error(serverError)

            // When
            viewModel.updateUser(
                userId = "123",
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
    }

    @Nested
    @DisplayName("Given error clearing")
    inner class ErrorClearingTests {

        @Test
        @DisplayName("When clearError is called, then should clear error state")
        fun `Given error state, When clearError called, Then should clear error state`() = runTest {
            // Given
            viewModel.updateUser(
                userId = "123",
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

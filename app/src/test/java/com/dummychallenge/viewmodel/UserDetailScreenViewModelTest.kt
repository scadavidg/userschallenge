package com.dummychallenge.viewmodel

import com.domain.models.Result
import com.domain.models.UserDetail
import com.domain.usecases.DeleteUserUseCase
import com.domain.usecases.GetUserDetailUseCase
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

@OptIn(ExperimentalCoroutinesApi::class)
class UserDetailScreenViewModelTest {

    private val getUserDetailUseCase: GetUserDetailUseCase = mockk()
    private val deleteUserUseCase: DeleteUserUseCase = mockk()
    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var viewModel: UserDetailScreenViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = UserDetailScreenViewModel(getUserDetailUseCase, deleteUserUseCase)
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
                location = null,
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

        @Test
        @DisplayName("When loadUserDetail is called multiple times, then should call use case each time")
        fun `Given multiple calls, When loadUserDetail called, Then should call use case each time`() = runTest {
            // Given
            val userId1 = "123"
            val userId2 = "456"
            val mockUserDetail1 = UserDetail(
                id = userId1,
                title = "mr",
                firstName = "John",
                lastName = "Doe",
                picture = "https://example.com/picture1.jpg",
                gender = "male",
                email = "john.doe@example.com",
                dateOfBirth = "1990-01-15T00:00:00.000Z",
                phone = "1234567890",
                country = "USA",
                location = null,
                registerDate = "2024-01-01T00:00:00.000Z",
                updatedDate = "2024-01-01T00:00:00.000Z"
            )
            val mockUserDetail2 = UserDetail(
                id = userId2,
                title = "ms",
                firstName = "Jane",
                lastName = "Smith",
                picture = "https://example.com/picture2.jpg",
                gender = "female",
                email = "jane.smith@example.com",
                dateOfBirth = "1992-05-20T00:00:00.000Z",
                phone = "9876543210",
                country = "Canada",
                location = null,
                registerDate = "2024-01-02T00:00:00.000Z",
                updatedDate = "2024-01-02T00:00:00.000Z"
            )
            coEvery { getUserDetailUseCase(userId1) } returns Result.Success(mockUserDetail1)
            coEvery { getUserDetailUseCase(userId2) } returns Result.Success(mockUserDetail2)

            // When
            viewModel.loadUserDetail(userId1)
            advanceUntilIdle()
            viewModel.loadUserDetail(userId2)
            advanceUntilIdle()

            // Then
            coVerify { getUserDetailUseCase(userId1) }
            coVerify { getUserDetailUseCase(userId2) }
            assert(viewModel.uiState.value.userDetail == mockUserDetail2)
        }
    }

    @Nested
    @DisplayName("Given error handling")
    inner class ErrorHandlingTests {

        @Test
        @DisplayName("When error occurs, then should show error state")
        fun `Given error state, When error occurs, Then should show error state`() = runTest {
            // Given
            val userId = "123"
            val errorMessage = "User not found"
            coEvery { getUserDetailUseCase(userId) } returns Result.Error(errorMessage)

            // When
            viewModel.loadUserDetail(userId)
            advanceUntilIdle()

            // Then
            assert(viewModel.uiState.value.error == errorMessage)
        }

        @Test
        @DisplayName("When loadUserDetail returns Loading, then should maintain loading state")
        fun `Given loading result, When loadUserDetail called, Then should maintain loading state`() = runTest {
            // Given
            val userId = "123"
            coEvery { getUserDetailUseCase(userId) } returns Result.Loading

            // When
            viewModel.loadUserDetail(userId)
            advanceUntilIdle()

            // Then
            assert(viewModel.uiState.value.isLoading)
            assert(viewModel.uiState.value.error == null)
            assert(viewModel.uiState.value.userDetail == null)
        }
    }

    @Nested
    @DisplayName("Given different user data scenarios")
    inner class UserDataScenariosTests {

        @Test
        @DisplayName("When user has complete data, then should load all fields correctly")
        fun `Given complete user data, When loadUserDetail succeeds, Then should load all fields correctly`() = runTest {
            // Given
            val userId = "123"
            val completeUserDetail = UserDetail(
                id = userId,
                title = "dr",
                firstName = "Dr. John",
                lastName = "Doe-Smith",
                picture = "https://example.com/doctor.jpg",
                gender = "male",
                email = "dr.john.doe@hospital.com",
                dateOfBirth = "1985-03-15T00:00:00.000Z",
                phone = "5551234567",
                country = "United States",
                location = null,
                registerDate = "2024-01-01T00:00:00.000Z",
                updatedDate = "2024-01-15T00:00:00.000Z"
            )
            coEvery { getUserDetailUseCase(userId) } returns Result.Success(completeUserDetail)

            // When
            viewModel.loadUserDetail(userId)
            advanceUntilIdle()

            // Then
            val loadedUser = viewModel.uiState.value.userDetail
            assert(loadedUser == completeUserDetail)
            assert(loadedUser?.title == "dr")
            assert(loadedUser?.firstName == "Dr. John")
            assert(loadedUser?.lastName == "Doe-Smith")
            assert(loadedUser?.email == "dr.john.doe@hospital.com")
            assert(loadedUser?.phone == "5551234567")
            assert(loadedUser?.country == "United States")
        }

        @Test
        @DisplayName("When user has minimal data, then should load available fields correctly")
        fun `Given minimal user data, When loadUserDetail succeeds, Then should load available fields correctly`() = runTest {
            // Given
            val userId = "456"
            val minimalUserDetail = UserDetail(
                id = userId,
                title = "mr",
                firstName = "John",
                lastName = "Doe",
                picture = "",
                gender = "male",
                email = "john@example.com",
                dateOfBirth = "",
                phone = "",
                country = null,
                location = null,
                registerDate = "",
                updatedDate = ""
            )
            coEvery { getUserDetailUseCase(userId) } returns Result.Success(minimalUserDetail)

            // When
            viewModel.loadUserDetail(userId)
            advanceUntilIdle()

            // Then
            val loadedUser = viewModel.uiState.value.userDetail
            assert(loadedUser == minimalUserDetail)
            assert(loadedUser?.picture == "")
            assert(loadedUser?.dateOfBirth == "")
            assert(loadedUser?.phone == "")
            assert(loadedUser?.country == null)
        }
    }
}

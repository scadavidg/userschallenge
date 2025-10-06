package com.dummychallenge.viewmodel

import com.domain.models.Result
import com.domain.models.UserList
import com.domain.models.UserPreview
import com.domain.usecases.DeleteUserUseCase
import com.domain.usecases.GetAllUserUseCase
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
import org.junit.jupiter.params.provider.ValueSource

@OptIn(ExperimentalCoroutinesApi::class)
class UserListScreenViewModelTest {

    private val getAllUserUseCase: GetAllUserUseCase = mockk()
    private val deleteUserUseCase: DeleteUserUseCase = mockk()
    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var viewModel: UserListScreenViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = UserListScreenViewModel(getAllUserUseCase, deleteUserUseCase)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Nested
    @DisplayName("Given initial state")
    inner class InitialStateTests {

        @Test
        @DisplayName("When viewModel is created, then it should load initial data")
        fun `Given viewModel creation, When initialized, Then should load initial data`() = runTest {
            // Given
            val mockUsers = listOf(
                UserPreview("1", "John", "Doe", "https://example.com/1.jpg", "https://example.com/picture1.jpg"),
                UserPreview("2", "Jane", "Smith", "https://example.com/2.jpg", "https://example.com/picture2.jpg")
            )
            val mockUserList = UserList(mockUsers, 0, 10, 2)
            coEvery { getAllUserUseCase(0) } returns Result.Success(mockUserList)

            // When
            advanceUntilIdle()

            // Then
            coVerify { getAllUserUseCase(0) }
            assert(viewModel.uiState.value.users.isNotEmpty())
            assert(viewModel.uiState.value.isLoading == false)
        }

        @Test
        @DisplayName("When initial data loading fails, then should show error state")
        fun `Given viewModel creation, When initial data loading fails, Then should show error state`() = runTest {
            // Given
            val errorMessage = "Network error"
            coEvery { getAllUserUseCase(0) } returns Result.Error(errorMessage)

            // When
            advanceUntilIdle()

            // Then
            assert(viewModel.uiState.value.error == errorMessage)
            assert(viewModel.uiState.value.isLoading == false)
            assert(viewModel.uiState.value.users.isEmpty())
        }
    }

    @Nested
    @DisplayName("Given user list loaded")
    inner class UserListLoadedTests {

        @BeforeEach
        fun setupWithLoadedUsers() = runTest {
            val mockUsers = listOf(
                UserPreview("1", "John", "Doe", "https://example.com/1.jpg", "https://example.com/picture1.jpg"),
                UserPreview("2", "Jane", "Smith", "https://example.com/2.jpg", "https://example.com/picture2.jpg"),
                UserPreview("3", "Bob", "Johnson", "https://example.com/3.jpg", "https://example.com/picture3.jpg")
            )
            val mockUserList = UserList(mockUsers, 0, 10, 3)
            coEvery { getAllUserUseCase(0) } returns Result.Success(mockUserList)
            advanceUntilIdle()
        }

        @Test
        @DisplayName("When loadMoreUsers is called, then should load next page")
        fun `Given user list loaded, When loadMoreUsers called, Then should load next page`() = runTest {
            // Given
            val nextPageUsers = listOf(
                UserPreview("4", "Alice", "Brown", "https://example.com/4.jpg", "https://example.com/picture4.jpg")
            )
            val nextPageUserList = UserList(nextPageUsers, 1, 10, 4)
            coEvery { getAllUserUseCase(1) } returns Result.Success(nextPageUserList)

            // When
            viewModel.loadMoreUsers()
            advanceUntilIdle()

            // Then
            coVerify { getAllUserUseCase(1) }
            assert(viewModel.uiState.value.users.size == 4)
        }

        @Test
        @DisplayName("When refresh is called, then should reload data from page 0")
        fun `Given user list loaded, When refresh called, Then should reload data from page 0`() = runTest {
            // Given
            val refreshedUsers = listOf(
                UserPreview("1", "John", "Doe", "https://example.com/1.jpg", "https://example.com/picture1.jpg"),
                UserPreview("2", "Jane", "Smith", "https://example.com/2.jpg", "https://example.com/picture2.jpg")
            )
            val refreshedUserList = UserList(refreshedUsers, 0, 10, 2)
            coEvery { getAllUserUseCase(0) } returns Result.Success(refreshedUserList)

            // When
            viewModel.refresh()
            advanceUntilIdle()

            // Then
            coVerify { getAllUserUseCase(0) }
            assert(viewModel.uiState.value.users.size == 2)
        }
    }

    @Nested
    @DisplayName("Given user deletion")
    inner class UserDeletionTests {

        @BeforeEach
        fun setupWithLoadedUsers() = runTest {
            val mockUsers = listOf(
                UserPreview("1", "John", "Doe", "https://example.com/1.jpg", "https://example.com/picture1.jpg"),
                UserPreview("2", "Jane", "Smith", "https://example.com/2.jpg", "https://example.com/picture2.jpg")
            )
            val mockUserList = UserList(mockUsers, 0, 10, 2)
            coEvery { getAllUserUseCase(0) } returns Result.Success(mockUserList)
            advanceUntilIdle()
        }

        @Test
        @DisplayName("When deleteUser is called successfully, then should remove user from list")
        fun `Given user list loaded, When deleteUser succeeds, Then should remove user from list`() = runTest {
            // Given
            val userId = "1"
            coEvery { deleteUserUseCase(userId) } returns Result.Success(Unit)

            // When
            viewModel.deleteUser()
            advanceUntilIdle()

            // Then
            coVerify { deleteUserUseCase(userId) }
            assert(viewModel.uiState.value.users.none { it.id == userId })
        }

        @Test
        @DisplayName("When deleteUser fails, then should show error and keep user in list")
        fun `Given user list loaded, When deleteUser fails, Then should show error and keep user in list`() = runTest {
            // Given
            val userId = "1"
            val errorMessage = "Delete failed"
            coEvery { deleteUserUseCase(userId) } returns Result.Error(errorMessage)

            // When
            viewModel.deleteUser()
            advanceUntilIdle()

            // Then
            assert(viewModel.uiState.value.error == errorMessage)
            assert(viewModel.uiState.value.users.any { it.id == userId })
        }

        @ParameterizedTest
        @ValueSource(strings = ["1", "2", "3"])
        @DisplayName("When deleteUser is called with different user IDs, then should handle each correctly")
        fun `Given user list loaded, When deleteUser called with different IDs, Then should handle each correctly`(userId: String) = runTest {
            // Given
            coEvery { deleteUserUseCase(userId) } returns Result.Success(Unit)

            // When
            viewModel.deleteUser()
            advanceUntilIdle()

            // Then
            coVerify { deleteUserUseCase(userId) }
        }
    }

    @Nested
    @DisplayName("Given error handling")
    inner class ErrorHandlingTests {

        @Test
        @DisplayName("When clearError is called, then should clear error state")
        fun `Given error state, When clearError called, Then should clear error state`() = runTest {
            // Given
            coEvery { getAllUserUseCase(0) } returns Result.Error("Test error")
            advanceUntilIdle()
            assert(viewModel.uiState.value.error != null)

            // When
            viewModel.clearError()

            // Then
            assert(viewModel.uiState.value.error == null)
        }
    }
}

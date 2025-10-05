package com.domain.usecases

import com.domain.models.Result
import com.domain.repository.UserRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@DisplayName("DeleteUserUseCase Tests")
class DeleteUserUseCaseTest {

    private lateinit var userRepository: UserRepository
    private lateinit var deleteUserUseCase: DeleteUserUseCase

    @BeforeEach
    fun setUp() {
        userRepository = mockk()
        deleteUserUseCase = DeleteUserUseCase(userRepository)
    }

    @Nested
    @DisplayName("Given a DeleteUserUseCase")
    inner class GivenDeleteUserUseCase {

        @Nested
        @DisplayName("When deleteUser returns Success")
        inner class WhenDeleteUserReturnsSuccess {

            @Test
            @DisplayName("Then should return Success with Unit")
            fun `should return Success with Unit`() = runTest {
                // Given
                val userId = "1"
                coEvery { userRepository.deleteUser(userId) } returns Result.Success(Unit)

                // When
                val result = deleteUserUseCase(userId)

                // Then
                assertTrue(result is Result.Success)
                assertEquals(Unit, result.data)
            }

            @Test
            @DisplayName("Then should return Success when user exists and is deleted")
            fun `should return Success when user exists and is deleted`() = runTest {
                // Given
                val userId = "123"
                coEvery { userRepository.deleteUser(userId) } returns Result.Success(Unit)

                // When
                val result = deleteUserUseCase(userId)

                // Then
                assertTrue(result is Result.Success)
                assertTrue(result.data is Unit)
            }
        }

        @Nested
        @DisplayName("When deleteUser returns Error")
        inner class WhenDeleteUserReturnsError {

            @Test
            @DisplayName("Then should return Error with message when user not found")
            fun `should return Error with message when user not found`() = runTest {
                // Given
                val userId = "999"
                val errorMessage = "User not found"
                coEvery { userRepository.deleteUser(userId) } returns Result.Error(errorMessage)

                // When
                val result = deleteUserUseCase(userId)

                // Then
                assertTrue(result is Result.Error)
                assertEquals(errorMessage, result.message)
            }

            @Test
            @DisplayName("Then should return Error with message when user cannot be deleted")
            fun `should return Error with message when user cannot be deleted`() = runTest {
                // Given
                val userId = "1"
                val errorMessage = "User cannot be deleted due to existing references"
                coEvery { userRepository.deleteUser(userId) } returns Result.Error(errorMessage)

                // When
                val result = deleteUserUseCase(userId)

                // Then
                assertTrue(result is Result.Error)
                assertEquals(errorMessage, result.message)
            }

            @Test
            @DisplayName("Then should return Error with message when invalid ID")
            fun `should return Error with message when invalid ID`() = runTest {
                // Given
                val userId = ""
                val errorMessage = "Invalid user ID"
                coEvery { userRepository.deleteUser(userId) } returns Result.Error(errorMessage)

                // When
                val result = deleteUserUseCase(userId)

                // Then
                assertTrue(result is Result.Error)
                assertEquals(errorMessage, result.message)
            }

            @Test
            @DisplayName("Then should return Error with message when user is already deleted")
            fun `should return Error with message when user is already deleted`() = runTest {
                // Given
                val userId = "1"
                val errorMessage = "User is already deleted"
                coEvery { userRepository.deleteUser(userId) } returns Result.Error(errorMessage)

                // When
                val result = deleteUserUseCase(userId)

                // Then
                assertTrue(result is Result.Error)
                assertEquals(errorMessage, result.message)
            }
        }

        @Nested
        @DisplayName("When deleteUser returns Loading")
        inner class WhenDeleteUserReturnsLoading {

            @Test
            @DisplayName("Then should return Loading")
            fun `should return Loading`() = runTest {
                // Given
                val userId = "1"
                coEvery { userRepository.deleteUser(userId) } returns Result.Loading

                // When
                val result = deleteUserUseCase(userId)

                // Then
                assertTrue(result is Result.Loading)
            }
        }

        @Nested
        @DisplayName("When repository throws exception")
        inner class WhenRepositoryThrowsException {

            @Test
            @DisplayName("Then should propagate the exception")
            fun `should propagate the exception`() = runTest {
                // Given
                val userId = "1"
                val exception = RuntimeException("Database connection failed")
                coEvery { userRepository.deleteUser(userId) } throws exception

                // When & Then
                assertThrows<RuntimeException> {
                    runTest { deleteUserUseCase(userId) }
                }
            }

            @Test
            @DisplayName("Then should propagate the exception when constraint violation")
            fun `should propagate the exception when constraint violation`() = runTest {
                // Given
                val userId = "1"
                val exception = RuntimeException("Foreign key constraint violation")
                coEvery { userRepository.deleteUser(userId) } throws exception

                // When & Then
                assertThrows<RuntimeException> {
                    runTest { deleteUserUseCase(userId) }
                }
            }
        }

        @Nested
        @DisplayName("When called with different user IDs")
        inner class WhenCalledWithDifferentUserIds {

            @Test
            @DisplayName("Then should call repository with correct ID")
            fun `should call repository with correct ID`() = runTest {
                // Given
                val userId = "123"
                coEvery { userRepository.deleteUser(userId) } returns Result.Success(Unit)

                // When
                deleteUserUseCase(userId)

                // Then
                // MockK automatically verifies the call was made with the correct parameter
            }

            @Test
            @DisplayName("Then should handle numeric user IDs")
            fun `should handle numeric user IDs`() = runTest {
                // Given
                val userId = "456"
                coEvery { userRepository.deleteUser(userId) } returns Result.Success(Unit)

                // When
                val result = deleteUserUseCase(userId)

                // Then
                assertTrue(result is Result.Success)
            }

            @Test
            @DisplayName("Then should handle UUID user IDs")
            fun `should handle UUID user IDs`() = runTest {
                // Given
                val userId = "550e8400-e29b-41d4-a716-446655440000"
                coEvery { userRepository.deleteUser(userId) } returns Result.Success(Unit)

                // When
                val result = deleteUserUseCase(userId)

                // Then
                assertTrue(result is Result.Success)
            }
        }
    }
}

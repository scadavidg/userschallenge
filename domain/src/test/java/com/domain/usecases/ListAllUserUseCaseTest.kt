package com.domain.usecases

import com.domain.models.Result
import com.domain.models.UserList
import com.domain.models.UserPreview
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

@DisplayName("ListAllUserUseCase Tests")
class ListAllUserUseCaseTest {

    private lateinit var userRepository: UserRepository
    private lateinit var listAllUserUseCase: GetAllUserUseCase

    @BeforeEach
    fun setUp() {
        userRepository = mockk()
        listAllUserUseCase = GetAllUserUseCase(userRepository)
    }

    @Nested
    @DisplayName("Given a ListAllUserUseCase")
    inner class GivenListAllUserUseCase {

        @Nested
        @DisplayName("When getAllUsers returns Success")
        inner class WhenGetAllUsersReturnsSuccess {

            @Test
            @DisplayName("Then should return Success with user list")
            fun `should return Success with user list`() = runTest {
                // Given
                val expectedUsers = listOf(
                    UserPreview(
                        id = "1",
                        title = "mr",
                        firstName = "John",
                        lastName = "Doe",
                        picture = "picture1.jpg",
                    ),
                    UserPreview(
                        id = "2",
                        title = "ms",
                        firstName = "Jane",
                        lastName = "Smith",
                        picture = "picture2.jpg",
                    )
                )
                val expectedUserList = UserList(
                    data = expectedUsers,
                    page = 0,
                    limit = 20,
                    total = 2
                )
                coEvery { userRepository.getAllUsers(0) } returns Result.Success(expectedUserList)

                // When
                val result = listAllUserUseCase(0)

                // Then
                assertTrue(result is Result.Success)
                assertEquals(expectedUserList, result.data)
            }

            @Test
            @DisplayName("Then should return Success with empty list when no users exist")
            fun `should return Success with empty list when no users exist`() = runTest {
                // Given
                val expectedUsers = emptyList<UserPreview>()
                val expectedUserList = UserList(
                    data = expectedUsers,
                    page = 0,
                    limit = 20,
                    total = 0
                )
                coEvery { userRepository.getAllUsers(0) } returns Result.Success(expectedUserList)

                // When
                val result = listAllUserUseCase(0)

                // Then
                assertTrue(result is Result.Success)
                assertEquals(expectedUserList, result.data)
            }
        }

        @Nested
        @DisplayName("When getAllUsers returns Error")
        inner class WhenGetAllUsersReturnsError {

            @Test
            @DisplayName("Then should return Error with message")
            fun `should return Error with message`() = runTest {
                // Given
                val errorMessage = "Failed to fetch users"
                coEvery { userRepository.getAllUsers(0) } returns Result.Error(errorMessage)

                // When
                val result = listAllUserUseCase(0)

                // Then
                assertTrue(result is Result.Error)
                assertEquals(errorMessage, result.message)
            }
        }

        @Nested
        @DisplayName("When getAllUsers returns Loading")
        inner class WhenGetAllUsersReturnsLoading {

            @Test
            @DisplayName("Then should return Loading")
            fun `should return Loading`() = runTest {
                // Given
                coEvery { userRepository.getAllUsers(0) } returns Result.Loading

                // When
                val result = listAllUserUseCase(0)

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
                val exception = RuntimeException("Database connection failed")
                coEvery { userRepository.getAllUsers(0) } throws exception

                // When & Then
                assertThrows<RuntimeException> {
                    runTest { listAllUserUseCase(0) }
                }
            }
        }
    }
}

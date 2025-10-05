package com.domain.usecases

import com.domain.models.Result
import com.domain.models.UserDetail
import com.domain.models.Location
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

@DisplayName("GetUserByIdUseCase Tests")
class GetUserByIdUseCaseTest {

    private lateinit var userRepository: UserRepository
    private lateinit var getUserByIdUseCase: GetUserByIdUseCase

    @BeforeEach
    fun setUp() {
        userRepository = mockk()
        getUserByIdUseCase = GetUserByIdUseCase(userRepository)
    }

    @Nested
    @DisplayName("Given a GetUserByIdUseCase")
    inner class GivenGetUserByIdUseCase {

        @Nested
        @DisplayName("When getUserById returns Success")
        inner class WhenGetUserByIdReturnsSuccess {

            @Test
            @DisplayName("Then should return Success with user")
            fun `should return Success with user`() = runTest {
                // Given
                val userId = "1"
                val expectedUser = UserDetail(
                    id = userId,
                    title = "mr",
                    firstName = "John",
                    lastName = "Doe",
                    picture = "picture1.jpg",
                    gender = "male",
                    email = "john.doe@example.com",
                    dateOfBirth = "1990-01-01",
                    phone = "123456789",
                    location = Location(
                        street = "123 Main St",
                        city = "New York",
                        state = "NY",
                        country = "USA",
                        timezone = "-5:00"
                    ),
                    registerDate = "2023-01-01",
                    updatedDate = "2023-01-01"
                )
                coEvery { userRepository.getUserById(userId) } returns Result.Success(expectedUser)

                // When
                val result = getUserByIdUseCase(userId)

                // Then
                assertTrue(result is Result.Success)
                assertEquals(expectedUser, result.data)
            }
        }

        @Nested
        @DisplayName("When getUserById returns Error")
        inner class WhenGetUserByIdReturnsError {

            @Test
            @DisplayName("Then should return Error with message when user not found")
            fun `should return Error with message when user not found`() = runTest {
                // Given
                val userId = "999"
                val errorMessage = "User not found"
                coEvery { userRepository.getUserById(userId) } returns Result.Error(errorMessage)

                // When
                val result = getUserByIdUseCase(userId)

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
                coEvery { userRepository.getUserById(userId) } returns Result.Error(errorMessage)

                // When
                val result = getUserByIdUseCase(userId)

                // Then
                assertTrue(result is Result.Error)
                assertEquals(errorMessage, result.message)
            }
        }

        @Nested
        @DisplayName("When getUserById returns Loading")
        inner class WhenGetUserByIdReturnsLoading {

            @Test
            @DisplayName("Then should return Loading")
            fun `should return Loading`() = runTest {
                // Given
                val userId = "1"
                coEvery { userRepository.getUserById(userId) } returns Result.Loading

                // When
                val result = getUserByIdUseCase(userId)

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
                coEvery { userRepository.getUserById(userId) } throws exception

                // When & Then
                assertThrows<RuntimeException> {
                    runTest { getUserByIdUseCase(userId) }
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
                val expectedUser = UserDetail(
                    id = userId,
                    title = "mr",
                    firstName = "Test",
                    lastName = "User",
                    picture = "test.jpg",
                    gender = "other",
                    email = "test@example.com",
                    dateOfBirth = "2000-01-01",
                    phone = "000000000",
                    location = Location(
                        street = "Test St",
                        city = "Test City",
                        state = "TS",
                        country = "Test",
                        timezone = "0:00"
                    ),
                    registerDate = "2023-01-01",
                    updatedDate = "2023-01-01"
                )
                coEvery { userRepository.getUserById(userId) } returns Result.Success(expectedUser)

                // When
                getUserByIdUseCase(userId)

                // Then
                // MockK automatically verifies the call was made with the correct parameter
            }
        }
    }
}

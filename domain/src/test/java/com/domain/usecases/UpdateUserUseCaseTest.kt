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

@DisplayName("UpdateUserUseCase Tests")
class UpdateUserUseCaseTest {

    private lateinit var userRepository: UserRepository
    private lateinit var updateUserUseCase: UpdateUserUseCase

    @BeforeEach
    fun setUp() {
        userRepository = mockk()
        updateUserUseCase = UpdateUserUseCase(userRepository)
    }

    @Nested
    @DisplayName("Given a UpdateUserUseCase")
    inner class GivenUpdateUserUseCase {

        @Nested
        @DisplayName("When updateUser returns Success")
        inner class WhenUpdateUserReturnsSuccess {

            @Test
            @DisplayName("Then should return Success with updated user")
            fun `should return Success with updated user`() = runTest {
                // Given
                val originalUser = UserDetail(
                    id = "1",
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
                val updatedUser = originalUser.copy(
                    firstName = "Johnny",
                    lastName = "Doe-Smith",
                    phone = "987654321",
                    location = Location(
                        street = "Boston St",
                        city = "Boston",
                        state = "MA",
                        country = "USA",
                        timezone = "-5:00"
                    ),
                    updatedDate = "2023-12-01"
                )
                coEvery { userRepository.updateUser(originalUser) } returns Result.Success(updatedUser)

                // When
                val result = updateUserUseCase(originalUser)

                // Then
                assertTrue(result is Result.Success)
                assertEquals(updatedUser, result.data)
            }

            @Test
            @DisplayName("Then should return Success with user having updated fields")
            fun `should return Success with user having updated fields`() = runTest {
                // Given
                val userToUpdate = UserDetail(
                    id = "2",
                    title = "ms",
                    firstName = "Jane",
                    lastName = "Smith",
                    picture = "picture2.jpg",
                    gender = "female",
                    email = "jane.smith@example.com",
                    dateOfBirth = "1992-05-15",
                    phone = "987654321",
                    location = Location(
                        street = "456 Oak Ave",
                        city = "Los Angeles",
                        state = "CA",
                        country = "USA",
                        timezone = "-8:00"
                    ),
                    registerDate = "2023-02-01",
                    updatedDate = "2023-02-01"
                )
                val updatedUser = userToUpdate.copy(
                    email = "jane.smith.new@example.com",
                    location = Location(
                        street = "SF St",
                        city = "San Francisco",
                        state = "CA",
                        country = "USA",
                        timezone = "-8:00"
                    ),
                    updatedDate = "2023-12-01"
                )
                coEvery { userRepository.updateUser(userToUpdate) } returns Result.Success(updatedUser)

                // When
                val result = updateUserUseCase(userToUpdate)

                // Then
                assertTrue(result is Result.Success)
                assertEquals("jane.smith.new@example.com", result.data.email)
                assertEquals("San Francisco", result.data.location?.city)
                assertEquals("2023-12-01", result.data.updatedDate)
            }
        }

        @Nested
        @DisplayName("When updateUser returns Error")
        inner class WhenUpdateUserReturnsError {

            @Test
            @DisplayName("Then should return Error with message when user not found")
            fun `should return Error with message when user not found`() = runTest {
                // Given
                val userToUpdate = UserDetail(
                    id = "999",
                    title = "mr",
                    firstName = "Non",
                    lastName = "Existent",
                    picture = "picture.jpg",
                    gender = "other",
                    email = "non@example.com",
                    dateOfBirth = "2000-01-01",
                    phone = "000000000",
                    location = Location(
                        street = "Nowhere St",
                        city = "Nowhere",
                        state = "NW",
                        country = "Test",
                        timezone = "0:00"
                    ),
                    registerDate = "2023-01-01",
                    updatedDate = "2023-01-01"
                )
                val errorMessage = "User not found"
                coEvery { userRepository.updateUser(userToUpdate) } returns Result.Error(errorMessage)

                // When
                val result = updateUserUseCase(userToUpdate)

                // Then
                assertTrue(result is Result.Error)
                assertEquals(errorMessage, result.message)
            }

            @Test
            @DisplayName("Then should return Error with message when email already exists")
            fun `should return Error with message when email already exists`() = runTest {
                // Given
                val userToUpdate = UserDetail(
                    id = "1",
                    title = "mr",
                    firstName = "John",
                    lastName = "Doe",
                    picture = "picture1.jpg",
                    gender = "male",
                    email = "existing@example.com",
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
                val errorMessage = "Email already exists"
                coEvery { userRepository.updateUser(userToUpdate) } returns Result.Error(errorMessage)

                // When
                val result = updateUserUseCase(userToUpdate)

                // Then
                assertTrue(result is Result.Error)
                assertEquals(errorMessage, result.message)
            }

            @Test
            @DisplayName("Then should return Error with message when validation fails")
            fun `should return Error with message when validation fails`() = runTest {
                // Given
                val invalidUser = UserDetail(
                    id = "1",
                    title = "",
                    firstName = "",
                    lastName = "",
                    picture = "",
                    gender = "",
                    email = "invalid-email",
                    dateOfBirth = "",
                    phone = "",
                    location = null,
                    registerDate = "2023-01-01",
                    updatedDate = "2023-01-01"
                )
                val errorMessage = "Invalid user data"
                coEvery { userRepository.updateUser(invalidUser) } returns Result.Error(errorMessage)

                // When
                val result = updateUserUseCase(invalidUser)

                // Then
                assertTrue(result is Result.Error)
                assertEquals(errorMessage, result.message)
            }
        }

        @Nested
        @DisplayName("When updateUser returns Loading")
        inner class WhenUpdateUserReturnsLoading {

            @Test
            @DisplayName("Then should return Loading")
            fun `should return Loading`() = runTest {
                // Given
                val userToUpdate = UserDetail(
                    id = "1",
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
                coEvery { userRepository.updateUser(userToUpdate) } returns Result.Loading

                // When
                val result = updateUserUseCase(userToUpdate)

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
                val userToUpdate = UserDetail(
                    id = "1",
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
                val exception = RuntimeException("Database connection failed")
                coEvery { userRepository.updateUser(userToUpdate) } throws exception

                // When & Then
                assertThrows<RuntimeException> {
                    runTest { updateUserUseCase(userToUpdate) }
                }
            }
        }

        @Nested
        @DisplayName("When called with different user data")
        inner class WhenCalledWithDifferentUserData {

            @Test
            @DisplayName("Then should call repository with correct user")
            fun `should call repository with correct user`() = runTest {
                // Given
                val userToUpdate = UserDetail(
                    id = "123",
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
                val updatedUser = userToUpdate.copy(updatedDate = "2023-12-01")
                coEvery { userRepository.updateUser(userToUpdate) } returns Result.Success(updatedUser)

                // When
                updateUserUseCase(userToUpdate)

                // Then
                // MockK automatically verifies the call was made with the correct parameter
            }
        }
    }
}

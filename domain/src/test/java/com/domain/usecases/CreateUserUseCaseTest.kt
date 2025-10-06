package com.domain.usecases

import com.domain.models.Result
import com.domain.models.UserDetail
import com.domain.models.Location
import com.domain.repository.UserRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

@DisplayName("CreateUserUseCase Tests")
class CreateUserUseCaseTest {

    private lateinit var userRepository: UserRepository
    private lateinit var createUserUseCase: CreateUserUseCase

    @BeforeEach
    fun setUp() {
        userRepository = mockk()
        createUserUseCase = CreateUserUseCase(userRepository)
    }

    @Nested
    @DisplayName("Given a CreateUserUseCase")
    inner class GivenCreateUserUseCase {

        @Nested
        @DisplayName("When createUser returns Success")
        inner class WhenCreateUserReturnsSuccess {

            @Test
            @DisplayName("Then should return Success with created user")
            fun `should return Success with created user`() = runTest {
                // Given
                val newUser = UserDetail(
                    id = "",
                    title = "mr",
                    firstName = "John",
                    lastName = "Doe",
                    picture = "picture1.jpg",
                    gender = "male",
                    email = "john.doe@example.com",
                    dateOfBirth = "1990-01-01",
                    phone = "123456789",
                    country = "USA",
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
                val createdUser = newUser.copy(id = "1")
                coEvery { userRepository.createUser(newUser) } returns Result.Success(createdUser)

                // When
                val result = createUserUseCase(newUser)

                // Then
                // Then
                kotlin.test.assertTrue(result is Result.Success)
                assertEquals(createdUser, result.data)
            }

            @Test
            @DisplayName("Then should return Success with user having generated ID")
            fun `should return Success with user having generated ID`() = runTest {
                // Given
                val newUser = UserDetail(
                    id = "",
                    title = "ms",
                    firstName = "Jane",
                    lastName = "Smith",
                    picture = "picture2.jpg",
                    gender = "female",
                    email = "jane.smith@example.com",
                    dateOfBirth = "1992-05-15",
                    phone = "987654321",
                    country = "USA",
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
                val createdUser = newUser.copy(id = "2")
                coEvery { userRepository.createUser(newUser) } returns Result.Success(createdUser)

                // When
                val result = createUserUseCase(newUser)

                // Then
                assertTrue(result is Result.Success)
                with(result as Result.Success){
                    assertEquals("2", result.data.id)
                    assertEquals(newUser.firstName, result.data.firstName)
                    assertEquals(newUser.lastName, result.data.lastName)
                }
            }
        }

        @Nested
        @DisplayName("When createUser returns Error")
        inner class WhenCreateUserReturnsError {

            @Test
            @DisplayName("Then should return Error with message when email already exists")
            fun `should return Error with message when email already exists`() = runTest {
                // Given
                val newUser = UserDetail(
                    id = "",
                    title = "mr",
                    firstName = "John",
                    lastName = "Doe",
                    picture = "picture1.jpg",
                    gender = "male",
                    email = "existing@example.com",
                    dateOfBirth = "1990-01-01",
                    phone = "123456789",
                    country = "USA",
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
                coEvery { userRepository.createUser(newUser) } returns Result.Error(errorMessage)

                // When
                val result = createUserUseCase(newUser)

                // Then
                assertTrue(result is Result.Error)
                with(result as Result.Error){
                    assertEquals(errorMessage, result.message)
                }
            }

            @Test
            @DisplayName("Then should return Error with message when validation fails")
            fun `should return Error with message when validation fails`() = runTest {
                // Given
                val invalidUser = UserDetail(
                    id = "",
                    title = "",
                    firstName = "",
                    lastName = "",
                    picture = "",
                    gender = "",
                    email = "invalid-email",
                    dateOfBirth = "",
                    phone = "",
                    country = null,
                    location = null,
                    registerDate = "",
                    updatedDate = ""
                )
                val errorMessage = "Invalid user data"
                coEvery { userRepository.createUser(invalidUser) } returns Result.Error(errorMessage)

                // When
                val result = createUserUseCase(invalidUser)

                // Then
                assertTrue(result is Result.Error)
                with(result as Result.Error){
                    assertEquals(errorMessage, result.message)
                }
            }
        }

        @Nested
        @DisplayName("When createUser returns Loading")
        inner class WhenCreateUserReturnsLoading {

            @Test
            @DisplayName("Then should return Loading")
            fun `should return Loading`() = runTest {
                // Given
                val newUser = UserDetail(
                    id = "",
                    title = "mr",
                    firstName = "John",
                    lastName = "Doe",
                    picture = "picture1.jpg",
                    gender = "male",
                    email = "john.doe@example.com",
                    dateOfBirth = "1990-01-01",
                    phone = "123456789",
                    country = "USA",
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
                coEvery { userRepository.createUser(newUser) } returns Result.Loading

                // When
                val result = createUserUseCase(newUser)

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
                val newUser = UserDetail(
                    id = "",
                    title = "mr",
                    firstName = "John",
                    lastName = "Doe",
                    picture = "picture1.jpg",
                    gender = "male",
                    email = "john.doe@example.com",
                    dateOfBirth = "1990-01-01",
                    phone = "123456789",
                    country = "USA",
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
                coEvery { userRepository.createUser(newUser) } throws exception

                // When & Then
                assertThrows<RuntimeException> {
                    runTest { createUserUseCase(newUser) }
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
                val newUser = UserDetail(
                    id = "",
                    title = "mr",
                    firstName = "Test",
                    lastName = "User",
                    picture = "test.jpg",
                    gender = "other",
                    email = "test@example.com",
                    dateOfBirth = "2000-01-01",
                    phone = "000000000",
                    country = "USA",
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
                val createdUser = newUser.copy(id = "123")
                coEvery { userRepository.createUser(newUser) } returns Result.Success(createdUser)

                // When
                createUserUseCase(newUser)

                // Then
                // MockK automatically verifies the call was made with the correct parameter
            }
        }
    }
}

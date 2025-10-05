package com.data.repository

import com.data.dto.ListResponseDto
import com.data.dto.LocationDto
import com.data.dto.UserFullDto
import com.data.dto.UserPreviewDto
import com.data.service.UserService
import com.domain.models.Location
import com.domain.models.Result
import com.domain.models.UserDetail
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@DisplayName("UserRepositoryImpl Tests")
class UserRepositoryImplTest {
    private lateinit var userService: UserService
    private lateinit var userRepository: UserRepositoryImpl

    @BeforeEach
    fun setUp() {
        // Given - Mock setup
        userService = mockk()
        userRepository = UserRepositoryImpl(userService)
    }

    @Nested
    @DisplayName("Given UserRepositoryImpl")
    inner class GivenUserRepositoryImpl {
        @Nested
        @DisplayName("When getAllUsers is called")
        inner class WhenGetAllUsersIsCalled {
            @Test
            @DisplayName("Then should return Success with user list when service succeeds")
            fun `should return Success with user list when service succeeds`() =
                runTest {
                    // Given
                    val expectedUsers =
                        listOf(
                            UserPreviewDto(
                                id = "1",
                                title = "mr",
                                firstName = "John",
                                lastName = "Doe",
                                picture = "https://example.com/picture1.jpg",
                            ),
                            UserPreviewDto(
                                id = "2",
                                title = "ms",
                                firstName = "Jane",
                                lastName = "Smith",
                                picture = "https://example.com/picture2.jpg",
                            ),
                        )
                    val listResponse =
                        ListResponseDto(
                            data = expectedUsers,
                            total = 100,
                            page = 0,
                            limit = 20,
                        )
                    val successResponse = Response.success(listResponse)

                    coEvery { userService.getAllUsers() } returns successResponse

                    // When
                    val result = userRepository.getAllUsers()

                    // Then
                    assertTrue(result is Result.Success)
                    assertEquals(2, result.data.size)
                    assertEquals("John", result.data[0].firstName)
                    assertEquals("Doe", result.data[0].lastName)
                    assertEquals("Jane", result.data[1].firstName)
                    assertEquals("Smith", result.data[1].lastName)
                }

            @Test
            @DisplayName("Then should return Error when service returns empty response")
            fun `should return Error when service returns empty response`() =
                runTest {
                    // Given
                    val successResponse = Response.success<ListResponseDto<UserPreviewDto>>(null)

                    coEvery { userService.getAllUsers() } returns successResponse

                    // When
                    val result = userRepository.getAllUsers()

                    // Then
                    assertTrue(result is Result.Error)
                    assertEquals("Empty response from server", result.message)
                }

            @Test
            @DisplayName("Then should return Error when service fails with HTTP error")
            fun `should return Error when service fails with HTTP error`() =
                runTest {
                    // Given
                    val errorResponse =
                        Response.error<ListResponseDto<UserPreviewDto>>(
                            500,
                            "Internal Server Error".toResponseBody("text/plain".toMediaType()),
                        )

                    coEvery { userService.getAllUsers() } returns errorResponse

                    // When
                    val result = userRepository.getAllUsers()

                    // Then
                    assertTrue(result is Result.Error)
                    assertTrue(result.message.contains("Failed to fetch users"))
                    assertTrue(result.message.contains("500"))
                }

            @Test
            @DisplayName("Then should return Error when HttpException is thrown")
            fun `should return Error when HttpException is thrown`() =
                runTest {
                    // Given
                    val httpException =
                        HttpException(
                            Response.error<ListResponseDto<UserPreviewDto>>(
                                404,
                                "Not Found".toResponseBody("text/plain".toMediaType()),
                            ),
                        )

                    coEvery { userService.getAllUsers() } throws httpException

                    // When
                    val result = userRepository.getAllUsers()

                    // Then
                    assertTrue(result is Result.Error)
                    assertTrue(result.message.contains("HTTP error"))
                    assertTrue(result.message.contains("404"))
                }

            @Test
            @DisplayName("Then should return Error when IOException is thrown")
            fun `should return Error when IOException is thrown`() =
                runTest {
                    // Given
                    val ioException = IOException("Network connection failed")

                    coEvery { userService.getAllUsers() } throws ioException

                    // When
                    val result = userRepository.getAllUsers()

                    // Then
                    assertTrue(result is Result.Error)
                    assertTrue(result.message.contains("Network error"))
                    assertTrue(result.message.contains("Network connection failed"))
                }

            @Test
            @DisplayName("Then should return Error when unexpected exception is thrown")
            fun `should return Error when unexpected exception is thrown`() =
                runTest {
                    // Given
                    val unexpectedException = RuntimeException("Unexpected error")

                    coEvery { userService.getAllUsers() } throws unexpectedException

                    // When
                    val result = userRepository.getAllUsers()

                    // Then
                    assertTrue(result is Result.Error)
                    assertTrue(result.message.contains("Unexpected error"))
                    assertTrue(result.message.contains("Unexpected error"))
                }
        }

        @Nested
        @DisplayName("When getUserById is called")
        inner class WhenGetUserByIdIsCalled {
            @Test
            @DisplayName("Then should return Success with user when service succeeds")
            fun `should return Success with user when service succeeds`() =
                runTest {
                    // Given
                    val userId = "1"
                    val expectedUser =
                        UserFullDto(
                            id = userId,
                            title = "mr",
                            firstName = "John",
                            lastName = "Doe",
                            picture = "https://example.com/picture.jpg",
                            gender = "male",
                            email = "john.doe@example.com",
                            dateOfBirth = "1990-01-01",
                            registerDate = "2023-01-01",
                            phone = "123456789",
                            location =
                                LocationDto(
                                    street = "123 Main St",
                                    city = "New York",
                                    state = "NY",
                                    country = "USA",
                                    timezone = "-5:00",
                                ),
                        )
                    val successResponse = Response.success(expectedUser)

                    coEvery { userService.getUserById(userId) } returns successResponse

                    // When
                    val result = userRepository.getUserById(userId)

                    // Then
                    assertTrue(result is Result.Success)
                    assertEquals(userId, result.data.id)
                    assertEquals("John", result.data.firstName)
                    assertEquals("Doe", result.data.lastName)
                    assertEquals("john.doe@example.com", result.data.email)
                    assertNotNull(result.data.location)
                }

            @Test
            @DisplayName("Then should return Error when user not found")
            fun `should return Error when user not found`() =
                runTest {
                    // Given
                    val userId = "999"
                    val errorResponse =
                        Response.error<UserFullDto>(
                            404,
                            "User not found".toResponseBody("text/plain".toMediaType()),
                        )

                    coEvery { userService.getUserById(userId) } returns errorResponse

                    // When
                    val result = userRepository.getUserById(userId)

                    // Then
                    assertTrue(result is Result.Error)
                    assertEquals("User not found", result.message)
                }

            @Test
            @DisplayName("Then should return Error when service returns empty response")
            fun `should return Error when service returns empty response`() =
                runTest {
                    // Given
                    val userId = "1"
                    val successResponse = Response.success<UserFullDto>(null)

                    coEvery { userService.getUserById(userId) } returns successResponse

                    // When
                    val result = userRepository.getUserById(userId)

                    // Then
                    assertTrue(result is Result.Error)
                    assertEquals("User not found", result.message)
                }

            @Test
            @DisplayName("Then should return Success with user when location is null")
            fun `should return Success with user when location is null`() =
                runTest {
                    // Given
                    val userId = "1"
                    val expectedUser =
                        UserFullDto(
                            id = userId,
                            title = "mr",
                            firstName = "John",
                            lastName = "Doe",
                            picture = "https://example.com/picture.jpg",
                            gender = "male",
                            email = "john.doe@example.com",
                            dateOfBirth = "1990-01-01",
                            registerDate = "2023-01-01",
                            phone = "123456789",
                            location = null,
                        )
                    val successResponse = Response.success(expectedUser)

                    coEvery { userService.getUserById(userId) } returns successResponse

                    // When
                    val result = userRepository.getUserById(userId)

                    // Then
                    assertTrue(result is Result.Success)
                    assertEquals(userId, result.data.id)
                    assertEquals("John", result.data.firstName)
                    assertEquals("Doe", result.data.lastName)
                    assertEquals("john.doe@example.com", result.data.email)
                    assertNull(result.data.location)
                }
        }

        @Nested
        @DisplayName("When createUser is called")
        inner class WhenCreateUserIsCalled {
            @Test
            @DisplayName("Then should return Success with created user when service succeeds")
            fun `should return Success with created user when service succeeds`() =
                runTest {
                    // Given
                    val newUser =
                        UserDetail(
                            id = "",
                            title = "mr",
                            firstName = "John",
                            lastName = "Doe",
                            picture = "https://example.com/picture.jpg",
                            gender = "male",
                            email = "john.doe@example.com",
                            dateOfBirth = "1990-01-01",
                            phone = "123456789",
                            location =
                                Location(
                                    street = "123 Main St",
                                    city = "New York",
                                    state = "NY",
                                    country = "USA",
                                    timezone = "-5:00",
                                ),
                            registerDate = "",
                            updatedDate = "",
                        )
                    val createdUser =
                        UserFullDto(
                            id = "1",
                            title = "mr",
                            firstName = "John",
                            lastName = "Doe",
                            picture = "https://example.com/picture.jpg",
                            gender = "male",
                            email = "john.doe@example.com",
                            dateOfBirth = "1990-01-01",
                            registerDate = "2023-01-01",
                            phone = "123456789",
                            location =
                                LocationDto(
                                    street = "123 Main St",
                                    city = "New York",
                                    state = "NY",
                                    country = "USA",
                                    timezone = "-5:00",
                                ),
                        )
                    val successResponse = Response.success(createdUser)

                    coEvery { userService.createUser(any()) } returns successResponse

                    // When
                    val result = userRepository.createUser(newUser)

                    // Then
                    assertTrue(result is Result.Success)
                    assertEquals("1", result.data.id)
                    assertEquals("John", result.data.firstName)
                    assertEquals("Doe", result.data.lastName)
                    assertEquals("john.doe@example.com", result.data.email)
                }

            @Test
            @DisplayName("Then should return Error when validation fails")
            fun `should return Error when validation fails`() =
                runTest {
                    // Given
                    val invalidUser =
                        UserDetail(
                            id = "",
                            title = "",
                            firstName = "",
                            lastName = "",
                            picture = "",
                            gender = "",
                            email = "invalid-email",
                            dateOfBirth = "",
                            phone = "",
                            location = null,
                            registerDate = "",
                            updatedDate = "",
                        )
                    val errorResponse =
                        Response.error<UserFullDto>(
                            400,
                            "Invalid user data".toResponseBody("text/plain".toMediaType()),
                        )

                    coEvery { userService.createUser(any()) } returns errorResponse

                    // When
                    val result = userRepository.createUser(invalidUser)

                    // Then
                    assertTrue(result is Result.Error)
                    assertTrue(result.message.contains("Invalid user data"))
                    assertTrue(result.message.contains("firstName, lastName, and email are required"))
                }

            @Test
            @DisplayName("Then should return Error when user already exists")
            fun `should return Error when user already exists`() =
                runTest {
                    // Given
                    val newUser =
                        UserDetail(
                            id = "",
                            title = "mr",
                            firstName = "John",
                            lastName = "Doe",
                            picture = "",
                            gender = "",
                            email = "existing@example.com",
                            dateOfBirth = "",
                            phone = "",
                            location = null,
                            registerDate = "",
                            updatedDate = "",
                        )
                    val errorResponse =
                        Response.error<UserFullDto>(
                            409,
                            "User already exists".toResponseBody("text/plain".toMediaType()),
                        )

                    coEvery { userService.createUser(any()) } returns errorResponse

                    // When
                    val result = userRepository.createUser(newUser)

                    // Then
                    assertTrue(result is Result.Error)
                    assertEquals("User already exists", result.message)
                }
        }

        @Nested
        @DisplayName("When updateUser is called")
        inner class WhenUpdateUserIsCalled {
            @Test
            @DisplayName("Then should return Success with updated user when service succeeds")
            fun `should return Success with updated user when service succeeds`() =
                runTest {
                    // Given
                    val userToUpdate =
                        UserDetail(
                            id = "1",
                            title = "mr",
                            firstName = "John",
                            lastName = "Doe-Updated",
                            picture = "https://example.com/picture.jpg",
                            gender = "male",
                            email = "john.doe@example.com",
                            dateOfBirth = "1990-01-01",
                            phone = "123456789",
                            location =
                                Location(
                                    street = "456 Updated St",
                                    city = "Boston",
                                    state = "MA",
                                    country = "USA",
                                    timezone = "-5:00",
                                ),
                            registerDate = "2023-01-01",
                            updatedDate = "2023-01-01",
                        )
                    val updatedUser =
                        UserFullDto(
                            id = "1",
                            title = "mr",
                            firstName = "John",
                            lastName = "Doe-Updated",
                            picture = "https://example.com/picture.jpg",
                            gender = "male",
                            email = "john.doe@example.com",
                            dateOfBirth = "1990-01-01",
                            registerDate = "2023-01-01",
                            phone = "123456789",
                            location =
                                LocationDto(
                                    street = "456 Updated St",
                                    city = "Boston",
                                    state = "MA",
                                    country = "USA",
                                    timezone = "-5:00",
                                ),
                        )
                    val successResponse = Response.success(updatedUser)

                    coEvery { userService.updateUser(any(), any()) } returns successResponse

                    // When
                    val result = userRepository.updateUser(userToUpdate)

                    // Then
                    assertTrue(result is Result.Success)
                    assertEquals("1", result.data.id)
                    assertEquals("Doe-Updated", result.data.lastName)
                    assertEquals("456 Updated St", result.data.location?.street)
                    assertEquals("Boston", result.data.location?.city)
                }

            @Test
            @DisplayName("Then should return Error when user not found")
            fun `should return Error when user not found`() =
                runTest {
                    // Given
                    val userToUpdate =
                        UserDetail(
                            id = "999",
                            title = "mr",
                            firstName = "John",
                            lastName = "Doe",
                            picture = "",
                            gender = "",
                            email = "john.doe@example.com",
                            dateOfBirth = "",
                            phone = "",
                            location = null,
                            registerDate = "",
                            updatedDate = "",
                        )
                    val errorResponse =
                        Response.error<UserFullDto>(
                            404,
                            "User not found".toResponseBody("text/plain".toMediaType()),
                        )

                    coEvery { userService.updateUser(any(), any()) } returns errorResponse

                    // When
                    val result = userRepository.updateUser(userToUpdate)

                    // Then
                    assertTrue(result is Result.Error)
                    assertEquals("User not found", result.message)
                }

            @Test
            @DisplayName("Then should return Error when email update is forbidden")
            fun `should return Error when email update is forbidden`() =
                runTest {
                    // Given
                    val userToUpdate =
                        UserDetail(
                            id = "1",
                            title = "mr",
                            firstName = "John",
                            lastName = "Doe",
                            picture = "",
                            gender = "",
                            email = "new-email@example.com",
                            dateOfBirth = "",
                            phone = "",
                            location = null,
                            registerDate = "",
                            updatedDate = "",
                        )
                    val errorResponse =
                        Response.error<UserFullDto>(
                            400,
                            "Email cannot be updated".toResponseBody("text/plain".toMediaType()),
                        )

                    coEvery { userService.updateUser(any(), any()) } returns errorResponse

                    // When
                    val result = userRepository.updateUser(userToUpdate)

                    // Then
                    assertTrue(result is Result.Error)
                    assertTrue(result.message.contains("Invalid user data"))
                    assertTrue(result.message.contains("email cannot be updated"))
                }
        }

        @Nested
        @DisplayName("When deleteUser is called")
        inner class WhenDeleteUserIsCalled {
            @Test
            @DisplayName("Then should return Success when user is deleted successfully")
            fun `should return Success when user is deleted successfully`() =
                runTest {
                    // Given
                    val userId = "1"
                    val successResponse = Response.success(userId)

                    coEvery { userService.deleteUser(userId) } returns successResponse

                    // When
                    val result = userRepository.deleteUser(userId)

                    // Then
                    assertTrue(result is Result.Success)
                    assertEquals(Unit, result.data)
                }

            @Test
            @DisplayName("Then should return Error when user not found")
            fun `should return Error when user not found`() =
                runTest {
                    // Given
                    val userId = "999"
                    val errorResponse =
                        Response.error<String>(
                            404,
                            "User not found".toResponseBody("text/plain".toMediaType()),
                        )

                    coEvery { userService.deleteUser(userId) } returns errorResponse

                    // When
                    val result = userRepository.deleteUser(userId)

                    // Then
                    assertTrue(result is Result.Error)
                    assertEquals("User not found", result.message)
                }

            @Test
            @DisplayName("Then should return Error when server returns unexpected response")
            fun `should return Error when server returns unexpected response`() =
                runTest {
                    // Given
                    val userId = "1"
                    val successResponse = Response.success("different-id")

                    coEvery { userService.deleteUser(userId) } returns successResponse

                    // When
                    val result = userRepository.deleteUser(userId)

                    // Then
                    assertTrue(result is Result.Error)
                    assertEquals("Unexpected response from server", result.message)
                }

            @Test
            @DisplayName("Then should return Error when server returns null response")
            fun `should return Error when server returns null response`() =
                runTest {
                    // Given
                    val userId = "1"
                    val successResponse = Response.success<String>(null)

                    coEvery { userService.deleteUser(userId) } returns successResponse

                    // When
                    val result = userRepository.deleteUser(userId)

                    // Then
                    assertTrue(result is Result.Error)
                    assertEquals("Unexpected response from server", result.message)
                }
        }
    }
}

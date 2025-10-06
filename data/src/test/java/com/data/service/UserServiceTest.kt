package com.data.service

import com.data.config.ApiConfig
import com.data.dto.DeleteUserResponseDto
import com.data.dto.ListResponseDto
import com.data.dto.LocationDto
import com.data.dto.UserCreateDto
import com.data.dto.UserFullDto
import com.data.dto.UserPreviewDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@DisplayName("UserService Tests")
class UserServiceTest {
    private lateinit var userService: UserService
    private lateinit var mockRetrofit: retrofit2.Retrofit

    @BeforeEach
    fun setUp() {
        // Given - Mock setup
        mockRetrofit = mockk()
        userService = mockk()
    }

    @Nested
    @DisplayName("Given UserService")
    inner class GivenUserService {
        @Nested
        @DisplayName("When getAllUsers is called")
        inner class WhenGetAllUsersIsCalled {
            @Test
            @DisplayName("Then should return successful response with user list")
            fun `should return successful response with user list`() =
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
                    val expectedResponse =
                        ListResponseDto(
                            data = expectedUsers,
                            total = 100,
                            page = 0,
                            limit = 20,
                        )
                    val successResponse = Response.success(expectedResponse)

                    coEvery {
                        userService.getAllUsers(
                            page = ApiConfig.DEFAULT_PAGE,
                            limit = ApiConfig.DEFAULT_LIMIT,
                            created = null,
                        )
                    } returns successResponse

                    // When
                    val result = userService.getAllUsers()

                    // Then
                    assertTrue(result.isSuccessful)
                    assertNotNull(result.body())
                    assertEquals(2, result.body()?.data?.size)
                    assertEquals(100, result.body()?.total)
                    assertEquals(0, result.body()?.page)
                    assertEquals(20, result.body()?.limit)
                    assertEquals("John", result.body()?.data?.get(0)?.firstName)
                    assertEquals("Jane", result.body()?.data?.get(1)?.firstName)
                }

            @Test
            @DisplayName("Then should return successful response with pagination")
            fun `should return successful response with pagination`() =
                runTest {
                    // Given
                    val expectedUsers =
                        listOf(
                            UserPreviewDto(
                                id = "3",
                                title = "dr",
                                firstName = "Bob",
                                lastName = "Johnson",
                                picture = "https://example.com/picture3.jpg",
                            ),
                        )
                    val expectedResponse =
                        ListResponseDto(
                            data = expectedUsers,
                            total = 100,
                            page = 1,
                            limit = 10,
                        )
                    val successResponse = Response.success(expectedResponse)

                    coEvery {
                        userService.getAllUsers(
                            page = 1,
                            limit = 10,
                            created = null,
                        )
                    } returns successResponse

                    // When
                    val result = userService.getAllUsers(page = 1, limit = 10)

                    // Then
                    assertTrue(result.isSuccessful)
                    assertNotNull(result.body())
                    assertEquals(1, result.body()?.data?.size)
                    assertEquals(100, result.body()?.total)
                    assertEquals(1, result.body()?.page)
                    assertEquals(10, result.body()?.limit)
                }

            @Test
            @DisplayName("Then should return error response when server fails")
            fun `should return error response when server fails`() =
                runTest {
                    // Given
                    val errorResponse =
                        Response.error<ListResponseDto<UserPreviewDto>>(
                            500,
                            "Internal Server Error".toResponseBody("text/plain".toMediaType()),
                        )

                    coEvery {
                        userService.getAllUsers(
                            page = ApiConfig.DEFAULT_PAGE,
                            limit = ApiConfig.DEFAULT_LIMIT,
                            created = null,
                        )
                    } returns errorResponse

                    // When
                    val result = userService.getAllUsers()

                    // Then
                    assertFalse(result.isSuccessful)
                    assertEquals(500, result.code())
                    assertEquals("Internal Server Error", result.errorBody()?.string())
                }
        }

        @Nested
        @DisplayName("When getUserById is called")
        inner class WhenGetUserByIdIsCalled {
            @Test
            @DisplayName("Then should return successful response with user data")
            fun `should return successful response with user data`() =
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
                    val result = userService.getUserById(userId)

                    // Then
                    assertTrue(result.isSuccessful)
                    assertNotNull(result.body())
                    assertEquals(userId, result.body()?.id)
                    assertEquals("John", result.body()?.firstName)
                    assertEquals("Doe", result.body()?.lastName)
                    assertEquals("john.doe@example.com", result.body()?.email)
                    assertNotNull(result.body()?.location)
                }

            @Test
            @DisplayName("Then should return error response when user not found")
            fun `should return error response when user not found`() =
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
                    val result = userService.getUserById(userId)

                    // Then
                    assertFalse(result.isSuccessful)
                    assertEquals(404, result.code())
                    assertEquals("User not found", result.errorBody()?.string())
                }
        }

        @Nested
        @DisplayName("When createUser is called")
        inner class WhenCreateUserIsCalled {
            @Test
            @DisplayName("Then should return successful response with created user")
            fun `should return successful response with created user`() =
                runTest {
                    // Given
                    val userCreateDto =
                        UserCreateDto(
                            firstName = "John",
                            lastName = "Doe",
                            email = "john.doe@example.com",
                            title = "mr",
                            gender = "male",
                            dateOfBirth = "1990-01-01",
                            phone = "123456789",
                            picture = "https://example.com/picture.jpg",
                            location =
                                LocationDto(
                                    street = "123 Main St",
                                    city = "New York",
                                    state = "NY",
                                    country = "USA",
                                    timezone = "-5:00",
                                ),
                        )
                    val expectedUser =
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
                    val successResponse = Response.success(expectedUser)

                    coEvery { userService.createUser(userCreateDto) } returns successResponse

                    // When
                    val result = userService.createUser(userCreateDto)

                    // Then
                    assertTrue(result.isSuccessful)
                    assertNotNull(result.body())
                    assertEquals("1", result.body()?.id)
                    assertEquals("John", result.body()?.firstName)
                    assertEquals("Doe", result.body()?.lastName)
                    assertEquals("john.doe@example.com", result.body()?.email)
                }

            @Test
            @DisplayName("Then should return error response when validation fails")
            fun `should return error response when validation fails`() =
                runTest {
                    // Given
                    val invalidUserCreateDto =
                        UserCreateDto(
                            firstName = "", // Invalid: empty
                            lastName = "", // Invalid: empty
                            email = "invalid-email", // Invalid: format
                            title = null,
                            gender = null,
                            dateOfBirth = null,
                            phone = null,
                            picture = null,
                            location = null,
                        )
                    val errorResponse =
                        Response.error<UserFullDto>(
                            400,
                            "Invalid user data - firstName, lastName, and email are required".toResponseBody("text/plain".toMediaType()),
                        )

                    coEvery { userService.createUser(invalidUserCreateDto) } returns errorResponse

                    // When
                    val result = userService.createUser(invalidUserCreateDto)

                    // Then
                    assertFalse(result.isSuccessful)
                    assertEquals(400, result.code())
                    assertEquals("Invalid user data - firstName, lastName, and email are required", result.errorBody()?.string())
                }
        }

        @Nested
        @DisplayName("When updateUser is called")
        inner class WhenUpdateUserIsCalled {
            @Test
            @DisplayName("Then should return successful response with updated user")
            fun `should return successful response with updated user`() =
                runTest {
                    // Given
                    val userId = "1"
                    val userUpdateDto =
                        UserFullDto(
                            id = userId,
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
                    val successResponse = Response.success(userUpdateDto)

                    coEvery { userService.updateUser(userId, userUpdateDto) } returns successResponse

                    // When
                    val result = userService.updateUser(userId, userUpdateDto)

                    // Then
                    assertTrue(result.isSuccessful)
                    assertNotNull(result.body())
                    assertEquals(userId, result.body()?.id)
                    assertEquals("Doe-Updated", result.body()?.lastName)
                    assertEquals("456 Updated St", result.body()?.location?.street)
                    assertEquals("Boston", result.body()?.location?.city)
                }

            @Test
            @DisplayName("Then should return error response when user not found")
            fun `should return error response when user not found`() =
                runTest {
                    // Given
                    val userId = "999"
                    val userUpdateDto =
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
                    val errorResponse =
                        Response.error<UserFullDto>(
                            404,
                            "User not found".toResponseBody("text/plain".toMediaType()),
                        )

                    coEvery { userService.updateUser(userId, userUpdateDto) } returns errorResponse

                    // When
                    val result = userService.updateUser(userId, userUpdateDto)

                    // Then
                    assertFalse(result.isSuccessful)
                    assertEquals(404, result.code())
                    assertEquals("User not found", result.errorBody()?.string())
                }
        }

        @Nested
        @DisplayName("When deleteUser is called")
        inner class WhenDeleteUserIsCalled {
            @Test
            @DisplayName("Then should return successful response with deleted user id")
            fun `should return successful response with deleted user id`() =
                runTest {
                    // Given
                    val userId = "1"
                    val successResponse = Response.success(DeleteUserResponseDto(userId))

                    coEvery { userService.deleteUser(userId) } returns successResponse

                    // When
                    val result = userService.deleteUser(userId)

                    // Then
                    assertTrue(result.isSuccessful)
                    assertNotNull(result.body())
                    assertEquals(userId, result.body()?.id)
                }

            @Test
            @DisplayName("Then should return error response when user not found")
            fun `should return error response when user not found`() =
                runTest {
                    // Given
                    val userId = "999"
                    val errorResponse =
                        Response.error<DeleteUserResponseDto>(
                            404,
                            "User not found".toResponseBody("text/plain".toMediaType()),
                        )

                    coEvery { userService.deleteUser(userId) } returns errorResponse

                    // When
                    val result = userService.deleteUser(userId)

                    // Then
                    assertFalse(result.isSuccessful)
                    assertEquals(404, result.code())
                    assertEquals("User not found", result.errorBody()?.string())
                }
        }
    }
}

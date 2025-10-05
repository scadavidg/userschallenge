package com.data.repository

import com.data.dto.ListResponseDto
import com.data.dto.UserFullDto
import com.data.dto.UserPreviewDto
import com.data.mapper.UserMapper.toCreateDto
import com.data.mapper.UserMapper.toDomain
import com.data.mapper.UserMapper.toFullDto
import com.data.service.UserService
import com.domain.models.Result
import com.domain.models.UserDetail
import com.domain.models.UserPreview
import com.domain.repository.UserRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class UserRepositoryImpl
    @Inject
    constructor(
        private val userService: UserService,
    ) : UserRepository {
        override suspend fun getAllUsers(): Result<List<UserPreview>> {
            return try {
                val response = userService.getAllUsers()
                if (response.isSuccessful) {
                    val listResponse: ListResponseDto<UserPreviewDto>? = response.body()
                    if (listResponse != null) {
                        val users = listResponse.data.map { it.toDomain() }
                        Result.Success(users)
                    } else {
                        Result.Error("Empty response from server")
                    }
                } else {
                    Result.Error("Failed to fetch users: ${response.code()} ${response.message()}")
                }
            } catch (e: HttpException) {
                Result.Error("HTTP error: ${e.code()} ${e.message()}")
            } catch (e: IOException) {
                Result.Error("Network error: ${e.message}")
            } catch (e: Exception) {
                Result.Error("Unexpected error: ${e.message}")
            }
        }

        override suspend fun getUserById(userId: String): Result<UserDetail> {
            return try {
                val response = userService.getUserById(userId)
                if (response.isSuccessful) {
                    val userDto: UserFullDto? = response.body()
                    if (userDto != null) {
                        Result.Success(userDto.toDomain())
                    } else {
                        Result.Error("User not found")
                    }
                } else {
                    when (response.code()) {
                        404 -> Result.Error("User not found")
                        else -> Result.Error("Failed to fetch user: ${response.code()} ${response.message()}")
                    }
                }
            } catch (e: HttpException) {
                when (e.code()) {
                    404 -> Result.Error("User not found")
                    else -> Result.Error("HTTP error: ${e.code()} ${e.message()}")
                }
            } catch (e: IOException) {
                Result.Error("Network error: ${e.message}")
            } catch (e: Exception) {
                Result.Error("Unexpected error: ${e.message}")
            }
        }

        override suspend fun createUser(userDetail: UserDetail): Result<UserDetail> {
            return try {
                val userCreateDto = userDetail.toCreateDto()
                val response = userService.createUser(userCreateDto)
                if (response.isSuccessful) {
                    val createdUserDto: UserFullDto? = response.body()
                    if (createdUserDto != null) {
                        Result.Success(createdUserDto.toDomain())
                    } else {
                        Result.Error("Empty response from server")
                    }
                } else {
                    when (response.code()) {
                        400 -> Result.Error("Invalid user data - firstName, lastName, and email are required")
                        409 -> Result.Error("User already exists")
                        else -> Result.Error("Failed to create user: ${response.code()} ${response.message()}")
                    }
                }
            } catch (e: HttpException) {
                when (e.code()) {
                    400 -> Result.Error("Invalid user data - firstName, lastName, and email are required")
                    409 -> Result.Error("User already exists")
                    else -> Result.Error("HTTP error: ${e.code()} ${e.message()}")
                }
            } catch (e: IOException) {
                Result.Error("Network error: ${e.message}")
            } catch (e: Exception) {
                Result.Error("Unexpected error: ${e.message}")
            }
        }

        override suspend fun updateUser(userDetail: UserDetail): Result<UserDetail> {
            return try {
                val userDto = userDetail.toFullDto()
                val response = userService.updateUser(userDetail.id, userDto)
                if (response.isSuccessful) {
                    val updatedUserDto: UserFullDto? = response.body()
                    if (updatedUserDto != null) {
                        Result.Success(updatedUserDto.toDomain())
                    } else {
                        Result.Error("Empty response from server")
                    }
                } else {
                    when (response.code()) {
                        400 -> Result.Error("Invalid user data - email cannot be updated")
                        404 -> Result.Error("User not found")
                        else -> Result.Error("Failed to update user: ${response.code()} ${response.message()}")
                    }
                }
            } catch (e: HttpException) {
                when (e.code()) {
                    400 -> Result.Error("Invalid user data - email cannot be updated")
                    404 -> Result.Error("User not found")
                    else -> Result.Error("HTTP error: ${e.code()} ${e.message()}")
                }
            } catch (e: IOException) {
                Result.Error("Network error: ${e.message}")
            } catch (e: Exception) {
                Result.Error("Unexpected error: ${e.message}")
            }
        }

        override suspend fun deleteUser(userId: String): Result<Unit> {
            return try {
                val response = userService.deleteUser(userId)
                if (response.isSuccessful) {
                    val deletedUserId: String? = response.body()
                    if (deletedUserId != null && deletedUserId == userId) {
                        Result.Success(Unit)
                    } else {
                        Result.Error("Unexpected response from server")
                    }
                } else {
                    when (response.code()) {
                        404 -> Result.Error("User not found")
                        else -> Result.Error("Failed to delete user: ${response.code()} ${response.message()}")
                    }
                }
            } catch (e: HttpException) {
                when (e.code()) {
                    404 -> Result.Error("User not found")
                    else -> Result.Error("HTTP error: ${e.code()} ${e.message()}")
                }
            } catch (e: IOException) {
                Result.Error("Network error: ${e.message}")
            } catch (e: Exception) {
                Result.Error("Unexpected error: ${e.message}")
            }
        }
    }

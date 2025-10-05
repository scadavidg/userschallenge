package com.domain.repository

import com.domain.models.Result
import com.domain.models.UserDetail
import com.domain.models.UserPreview

interface UserRepository {
    suspend fun getAllUsers(): Result<List<UserPreview>>
    suspend fun getUserById(userId: String): Result<UserDetail>
    suspend fun createUser(userDetail: UserDetail): Result<UserDetail>
    suspend fun updateUser(userDetail: UserDetail): Result<UserDetail>
    suspend fun deleteUser(userId: String): Result<Unit>
}
package com.domain.usecases

import com.domain.models.Result
import com.domain.models.UserDetail
import com.domain.repository.UserRepository

class GetUserDetailUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(userId: String): Result<UserDetail> {
        return userRepository.getUserById(userId)
    }
}

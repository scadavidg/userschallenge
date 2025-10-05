package com.domain.usecases

import com.domain.repository.UserRepository
import com.domain.models.Result

class DeleteUserUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(userId: String): Result<Unit> {
        return userRepository.deleteUser(userId)
    }
}

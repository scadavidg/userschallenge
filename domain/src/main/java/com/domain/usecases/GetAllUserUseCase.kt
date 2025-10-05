package com.domain.usecases

import com.domain.models.Result
import com.domain.models.UserPreview
import com.domain.repository.UserRepository

class GetAllUserUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(): Result<List<UserPreview>> {
        return userRepository.getAllUsers()
    }
}
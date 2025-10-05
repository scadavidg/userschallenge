package com.domain.usecases

import com.domain.models.Result
import com.domain.models.UserDetail
import com.domain.repository.UserRepository

class UpdateUserUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(userDetail: UserDetail): Result<UserDetail> {
        return userRepository.updateUser(userDetail)
    }
}

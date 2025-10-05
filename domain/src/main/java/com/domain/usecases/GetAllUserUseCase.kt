package com.domain.usecases

import com.domain.models.Result
import com.domain.models.UserList
import com.domain.repository.UserRepository

/**
 * Use case for retrieving paginated user list.
 * Use cases encapsulate business logic and are part of Clean Architecture.
 * The operator fun invoke allows calling this class like a function.
 */
class GetAllUserUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(page: Int): Result<UserList> {
        return userRepository.getAllUsers(page)
    }
}
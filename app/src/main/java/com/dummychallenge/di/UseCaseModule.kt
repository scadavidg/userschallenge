package com.dummychallenge.di

import com.domain.repository.UserRepository
import com.domain.usecases.CreateUserUseCase
import com.domain.usecases.DeleteUserUseCase
import com.domain.usecases.GetAllUserUseCase
import com.domain.usecases.GetUserDetailUseCase
import com.domain.usecases.UpdateUserUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides Use Case dependencies.
 * Use Cases are created by injecting the repository dependency.
 */
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideCreateUserUseCase(repository: UserRepository): CreateUserUseCase = CreateUserUseCase(repository)

    @Provides
    @Singleton
    fun provideUpdateUserUseCase(repository: UserRepository): UpdateUserUseCase = UpdateUserUseCase(repository)

    @Provides
    @Singleton
    fun provideDeleteUserUseCase(repository: UserRepository): DeleteUserUseCase = DeleteUserUseCase(repository)

    @Provides
    @Singleton
    fun provideGetAllUserUseCase(repository: UserRepository): GetAllUserUseCase = GetAllUserUseCase(repository)

    @Provides
    @Singleton
    fun provideGetUserDetailUseCase(repository: UserRepository): GetUserDetailUseCase = GetUserDetailUseCase(repository)
}
package com.domain.models

/**
 * Sealed class representing the possible states of an operation result.
 * Sealed classes are like enums but can hold data and are more flexible.
 * They ensure type safety and exhaustive when expressions.
 */
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
    object Loading : Result<Nothing>()
}
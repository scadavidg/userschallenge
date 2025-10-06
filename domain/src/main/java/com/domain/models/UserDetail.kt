package com.domain.models

data class UserDetail(
    val id: String,
    val title: String,
    val firstName: String,
    val lastName: String,
    val picture: String,
    val gender: String,
    val email: String,
    val dateOfBirth: String,
    val phone: String,
    val country: String?,
    val location: Location?,
    val registerDate: String,
    val updatedDate: String
)

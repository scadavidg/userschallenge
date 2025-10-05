package com.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserCreateDto(
    @Json(name = "firstName")
    val firstName: String,
    @Json(name = "lastName")
    val lastName: String,
    @Json(name = "email")
    val email: String,
    @Json(name = "title")
    val title: String? = null,
    @Json(name = "gender")
    val gender: String? = null,
    @Json(name = "dateOfBirth")
    val dateOfBirth: String? = null,
    @Json(name = "phone")
    val phone: String? = null,
    @Json(name = "picture")
    val picture: String? = null,
    @Json(name = "location")
    val location: LocationDto? = null,
)

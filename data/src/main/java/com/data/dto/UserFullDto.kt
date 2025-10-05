package com.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserFullDto(
    @Json(name = "id")
    val id: String,
    @Json(name = "title")
    val title: String,
    @Json(name = "firstName")
    val firstName: String,
    @Json(name = "lastName")
    val lastName: String,
    @Json(name = "gender")
    val gender: String,
    @Json(name = "email")
    val email: String,
    @Json(name = "dateOfBirth")
    val dateOfBirth: String,
    @Json(name = "registerDate")
    val registerDate: String,
    @Json(name = "phone")
    val phone: String,
    @Json(name = "picture")
    val picture: String,
    @Json(name = "location")
    val location: LocationDto?,
)

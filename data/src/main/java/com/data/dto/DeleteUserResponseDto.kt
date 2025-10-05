package com.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DeleteUserResponseDto(
    @Json(name = "id")
    val id: String,
)

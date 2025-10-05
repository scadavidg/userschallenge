package com.data.config

object ApiConfig {
    // Base Configuration
    const val BASE_URL = "https://dummyapi.io/data/v1/"
    const val CONNECT_TIMEOUT_SECONDS = 30L
    const val READ_TIMEOUT_SECONDS = 30L
    const val WRITE_TIMEOUT_SECONDS = 30L
    
    // API Key Configuration
    const val API_KEY_HEADER = "app-id"
    const val API_KEY_VALUE = "63473330c1927d386ca6a3a5"
    
    // Default Images
    const val DEFAULT_PROFILE_IMAGE_URL = "https://st4.depositphotos.com/29453910/37778/v/450/depositphotos_377785406-stock-illustration-hand-drawn-modern-man-avatar.jpg"

    // User Endpoints
    const val USER_ENDPOINT = "user"
    const val USER_BY_ID_ENDPOINT = "user/{id}"
    const val USER_CREATE_ENDPOINT = "user/create"
    const val USER_UPDATE_ENDPOINT = "user/{id}"
    const val USER_DELETE_ENDPOINT = "user/{id}"

    // Query Parameters
    const val PAGE_PARAM = "page"
    const val LIMIT_PARAM = "limit"
    const val CREATED_PARAM = "created"
    const val ID_PARAM = "id"

    // Default Values
    const val DEFAULT_PAGE = 0
    const val DEFAULT_LIMIT = 20
}

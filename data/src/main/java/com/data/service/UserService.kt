package com.data.service

import com.data.config.ApiConfig
import com.data.dto.ListResponseDto
import com.data.dto.UserCreateDto
import com.data.dto.UserFullDto
import com.data.dto.UserPreviewDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface UserService {
    /**
     * Get list of users sorted by registration date.
     * Pagination query params available.
     * Created query params available.
     */
    @GET(ApiConfig.USER_ENDPOINT)
    suspend fun getAllUsers(
        @Query(ApiConfig.PAGE_PARAM) page: Int = ApiConfig.DEFAULT_PAGE,
        @Query(ApiConfig.LIMIT_PARAM) limit: Int = ApiConfig.DEFAULT_LIMIT,
        @Query(ApiConfig.CREATED_PARAM) created: String? = null,
    ): Response<ListResponseDto<UserPreviewDto>>

    /**
     * Get user full data by user id
     */
    @GET(ApiConfig.USER_BY_ID_ENDPOINT)
    suspend fun getUserById(
        @Path(ApiConfig.ID_PARAM) id: String,
    ): Response<UserFullDto>

    /**
     * Create new user, return created user data.
     * Body: User Create (firstName, lastName, email are required)
     */
    @POST(ApiConfig.USER_CREATE_ENDPOINT)
    suspend fun createUser(
        @Body user: UserCreateDto,
    ): Response<UserFullDto>

    /**
     * Update user by id, return updated User data
     * Body: User data, only fields that should be updated. (email is forbidden to update)
     */
    @PUT(ApiConfig.USER_UPDATE_ENDPOINT)
    suspend fun updateUser(
        @Path(ApiConfig.ID_PARAM) id: String,
        @Body user: UserFullDto,
    ): Response<UserFullDto>

    /**
     * Delete user by id, return id of deleted user
     */
    @DELETE(ApiConfig.USER_DELETE_ENDPOINT)
    suspend fun deleteUser(
        @Path(ApiConfig.ID_PARAM) id: String,
    ): Response<String>
}

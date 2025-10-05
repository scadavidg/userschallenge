package com.data.config

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@DisplayName("ApiConfig Tests")
class ApiConfigTest {
    @Nested
    @DisplayName("Given ApiConfig")
    inner class GivenApiConfig {
        @Nested
        @DisplayName("When accessing base configuration")
        inner class WhenAccessingBaseConfiguration {
            @Test
            @DisplayName("Then should have correct base URL")
            fun `should have correct base URL`() {
                // When & Then
                assertEquals("https://dummyapi.io/data/v1/", ApiConfig.BASE_URL)
            }

            @Test
            @DisplayName("Then should have correct timeout values")
            fun `should have correct timeout values`() {
                // When & Then
                assertEquals(30L, ApiConfig.CONNECT_TIMEOUT_SECONDS)
                assertEquals(30L, ApiConfig.READ_TIMEOUT_SECONDS)
                assertEquals(30L, ApiConfig.WRITE_TIMEOUT_SECONDS)
            }
        }

        @Nested
        @DisplayName("When accessing user endpoints")
        inner class WhenAccessingUserEndpoints {
            @Test
            @DisplayName("Then should have correct user endpoint")
            fun `should have correct user endpoint`() {
                // When & Then
                assertEquals("user", ApiConfig.USER_ENDPOINT)
            }

            @Test
            @DisplayName("Then should have correct user by id endpoint")
            fun `should have correct user by id endpoint`() {
                // When & Then
                assertEquals("user/{id}", ApiConfig.USER_BY_ID_ENDPOINT)
            }

            @Test
            @DisplayName("Then should have correct user create endpoint")
            fun `should have correct user create endpoint`() {
                // When & Then
                assertEquals("user/create", ApiConfig.USER_CREATE_ENDPOINT)
            }

            @Test
            @DisplayName("Then should have correct user update endpoint")
            fun `should have correct user update endpoint`() {
                // When & Then
                assertEquals("user/{id}", ApiConfig.USER_UPDATE_ENDPOINT)
            }

            @Test
            @DisplayName("Then should have correct user delete endpoint")
            fun `should have correct user delete endpoint`() {
                // When & Then
                assertEquals("user/{id}", ApiConfig.USER_DELETE_ENDPOINT)
            }
        }

        @Nested
        @DisplayName("When accessing query parameters")
        inner class WhenAccessingQueryParameters {
            @Test
            @DisplayName("Then should have correct page parameter")
            fun `should have correct page parameter`() {
                // When & Then
                assertEquals("page", ApiConfig.PAGE_PARAM)
            }

            @Test
            @DisplayName("Then should have correct limit parameter")
            fun `should have correct limit parameter`() {
                // When & Then
                assertEquals("limit", ApiConfig.LIMIT_PARAM)
            }

            @Test
            @DisplayName("Then should have correct created parameter")
            fun `should have correct created parameter`() {
                // When & Then
                assertEquals("created", ApiConfig.CREATED_PARAM)
            }

            @Test
            @DisplayName("Then should have correct id parameter")
            fun `should have correct id parameter`() {
                // When & Then
                assertEquals("id", ApiConfig.ID_PARAM)
            }
        }

        @Nested
        @DisplayName("When accessing API key configuration")
        inner class WhenAccessingApiKeyConfiguration {
            @Test
            @DisplayName("Then should have correct API key header")
            fun `should have correct API key header`() {
                // When & Then
                assertEquals("app-id", ApiConfig.API_KEY_HEADER)
            }

            @Test
            @DisplayName("Then should have correct API key value")
            fun `should have correct API key value`() {
                // When & Then
                assertEquals("63473330c1927d386ca6a3a5", ApiConfig.API_KEY_VALUE)
            }
        }

        @Nested
        @DisplayName("When accessing default values")
        inner class WhenAccessingDefaultValues {
            @Test
            @DisplayName("Then should have correct default page")
            fun `should have correct default page`() {
                // When & Then
                assertEquals(0, ApiConfig.DEFAULT_PAGE)
            }

            @Test
            @DisplayName("Then should have correct default limit")
            fun `should have correct default limit`() {
                // When & Then
                assertEquals(20, ApiConfig.DEFAULT_LIMIT)
            }
        }

        @Nested
        @DisplayName("When validating configuration consistency")
        inner class WhenValidatingConfigurationConsistency {
            @Test
            @DisplayName("Then should have consistent endpoint patterns")
            fun `should have consistent endpoint patterns`() {
                // When & Then
                assertTrue(ApiConfig.USER_BY_ID_ENDPOINT.contains("{id}"))
                assertTrue(ApiConfig.USER_UPDATE_ENDPOINT.contains("{id}"))
                assertTrue(ApiConfig.USER_DELETE_ENDPOINT.contains("{id}"))
                assertTrue(ApiConfig.USER_CREATE_ENDPOINT.endsWith("create"))
            }

            @Test
            @DisplayName("Then should have valid timeout values")
            fun `should have valid timeout values`() {
                // When & Then
                assertTrue(ApiConfig.CONNECT_TIMEOUT_SECONDS > 0)
                assertTrue(ApiConfig.READ_TIMEOUT_SECONDS > 0)
                assertTrue(ApiConfig.WRITE_TIMEOUT_SECONDS > 0)
            }

            @Test
            @DisplayName("Then should have valid default values")
            fun `should have valid default values`() {
                // When & Then
                assertTrue(ApiConfig.DEFAULT_PAGE >= 0)
                assertTrue(ApiConfig.DEFAULT_LIMIT > 0)
            }

            @Test
            @DisplayName("Then should have non-empty string constants")
            fun `should have non empty string constants`() {
                // When & Then
                assertTrue(ApiConfig.BASE_URL.isNotEmpty())
                assertTrue(ApiConfig.USER_ENDPOINT.isNotEmpty())
                assertTrue(ApiConfig.USER_BY_ID_ENDPOINT.isNotEmpty())
                assertTrue(ApiConfig.USER_CREATE_ENDPOINT.isNotEmpty())
                assertTrue(ApiConfig.USER_UPDATE_ENDPOINT.isNotEmpty())
                assertTrue(ApiConfig.USER_DELETE_ENDPOINT.isNotEmpty())
                assertTrue(ApiConfig.PAGE_PARAM.isNotEmpty())
                assertTrue(ApiConfig.LIMIT_PARAM.isNotEmpty())
                assertTrue(ApiConfig.CREATED_PARAM.isNotEmpty())
                assertTrue(ApiConfig.ID_PARAM.isNotEmpty())
                assertTrue(ApiConfig.API_KEY_HEADER.isNotEmpty())
                assertTrue(ApiConfig.API_KEY_VALUE.isNotEmpty())
            }
        }
    }
}

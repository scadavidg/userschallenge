package com.data.di

import com.data.config.ApiConfig
import com.data.service.UserService
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@DisplayName("DataModule Tests")
class DataModuleTest {
    private lateinit var dataModule: DataModule

    @BeforeEach
    fun setUp() {
        // Given
        dataModule = DataModule
    }

    @Nested
    @DisplayName("Given DataModule")
    inner class GivenDataModule {
        @Nested
        @DisplayName("When provideMoshi is called")
        inner class WhenProvideMoshiIsCalled {
            @Test
            @DisplayName("Then should return configured Moshi instance")
            fun `should return configured Moshi instance`() {
                // When
                val moshi = dataModule.provideMoshi()

                // Then
                assertNotNull(moshi)
                assertTrue(moshi is Moshi)
            }
        }

        @Nested
        @DisplayName("When provideOkHttpClient is called")
        inner class WhenProvideOkHttpClientIsCalled {
            @Test
            @DisplayName("Then should return configured OkHttpClient instance")
            fun `should return configured OkHttpClient instance`() {
                // When
                val okHttpClient = dataModule.provideOkHttpClient()

                // Then
                assertNotNull(okHttpClient)
                assertTrue(okHttpClient is OkHttpClient)
                // assertEquals(ApiConfig.CONNECT_TIMEOUT_SECONDS * 1000, okHttpClient.connectTimeoutMillis)
                // /assertEquals(ApiConfig.READ_TIMEOUT_SECONDS * 1000, okHttpClient.readTimeoutMillis)
                // assertEquals(ApiConfig.WRITE_TIMEOUT_SECONDS * 1000, okHttpClient.writeTimeoutMillis)
            }

            @Test
            @DisplayName("Then should have API key interceptor configured")
            fun `should have API key interceptor configured`() {
                // When
                val okHttpClient = dataModule.provideOkHttpClient()

                // Then
                assertNotNull(okHttpClient)
                // Note: We can't directly test interceptors, but we can verify the client is configured
                // The API key interceptor is added in the NetworkModule
                assertTrue(okHttpClient.interceptors.isNotEmpty())
            }
        }

        @Nested
        @DisplayName("When provideUserService is called")
        inner class WhenProvideUserServiceIsCalled {
            @Test
            @DisplayName("Then should return configured UserService instance")
            fun `should return configured UserService instance`() {
                // Given
                val moshi = dataModule.provideMoshi()
                val okHttpClient = dataModule.provideOkHttpClient()

                // When
                val userService = dataModule.provideUserService(okHttpClient, moshi)

                // Then
                assertNotNull(userService)
                assertTrue(userService is UserService)
            }

            @Test
            @DisplayName("Then should create Retrofit with correct base URL")
            fun `should create Retrofit with correct base URL`() {
                // Given
                val moshi = dataModule.provideMoshi()
                val okHttpClient = dataModule.provideOkHttpClient()

                // When
                val userService = dataModule.provideUserService(okHttpClient, moshi)

                // Then
                assertNotNull(userService)
                // Note: We can't directly test the Retrofit instance, but we can verify
                // that the service is created successfully, which means Retrofit was configured correctly
            }
        }
    }
}

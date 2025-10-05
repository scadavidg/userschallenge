package com.data.di

import com.data.config.ApiConfig
import com.data.service.UserService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Hilt module for providing network-related dependencies.
 * Modules tell Hilt how to create dependencies that can't be constructor-injected.
 * SingletonComponent ensures these dependencies live for the entire app lifecycle.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                },
            )
            .addInterceptor(createApiKeyInterceptor())
            .connectTimeout(ApiConfig.CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(ApiConfig.READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Creates an interceptor that adds API key header to all requests.
     * Interceptors modify requests/responses before they are sent/received.
     */
    private fun createApiKeyInterceptor(): Interceptor {
        return Interceptor { chain ->
            val originalRequest = chain.request()
            val newRequest = originalRequest.newBuilder()
                .addHeader(ApiConfig.API_KEY_HEADER, ApiConfig.API_KEY_VALUE)
                .build()
            chain.proceed(newRequest)
        }
    }

    @Provides
    @Singleton
    fun provideUserService(
        okHttpClient: OkHttpClient,
        moshi: Moshi,
    ): UserService {
        return Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(UserService::class.java)
    }
}

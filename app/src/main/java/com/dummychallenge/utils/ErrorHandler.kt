package com.dummychallenge.utils

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Utility class to handle API error types and provide user-friendly messages
 */
@Singleton
class ErrorHandler @Inject constructor(
    private val crashlyticsLogger: CrashlyticsLogger
) {
    
    /**
     * Enum representing different API error types
     */
    enum class ApiErrorType(val errorCode: String) {
        APP_ID_NOT_EXIST("APP_ID_NOT_EXIST"),
        APP_ID_MISSING("APP_ID_MISSING"),
        PARAMS_NOT_VALID("PARAMS_NOT_VALID"),
        BODY_NOT_VALID("BODY_NOT_VALID"),
        RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND"),
        PATH_NOT_FOUND("PATH_NOT_FOUND"),
        SERVER_ERROR("SERVER_ERROR"),
        UNKNOWN("UNKNOWN")
    }
    
    /**
     * Data class representing an API error response
     */
    data class ApiError(
        val error: String
    )
    
    /**
     * Get user-friendly error message based on API error type
     */
    fun getUserFriendlyMessage(errorType: ApiErrorType): String {
        // Log error to Crashlytics
        crashlyticsLogger.log("API Error: ${errorType.errorCode}")
        crashlyticsLogger.setCustomKey("api_error_type", errorType.errorCode)
        
        return when (errorType) {
            ApiErrorType.APP_ID_NOT_EXIST -> 
                "Authentication error. Please contact support."
            ApiErrorType.APP_ID_MISSING -> 
                "Authentication error. Please contact support."
            ApiErrorType.PARAMS_NOT_VALID -> 
                "Invalid request parameters. Please try again."
            ApiErrorType.BODY_NOT_VALID -> 
                "Invalid data format. Please check your input and try again."
            ApiErrorType.RESOURCE_NOT_FOUND -> 
                "The requested user was not found. It may have been deleted."
            ApiErrorType.PATH_NOT_FOUND -> 
                "Service temporarily unavailable. Please try again later."
            ApiErrorType.SERVER_ERROR -> 
                "Server error occurred. Please try again later."
            ApiErrorType.UNKNOWN -> 
                "An unexpected error occurred. Please try again."
        }
    }
    
    /**
     * Parse API error from JSON string
     */
    fun parseApiError(errorJson: String): ApiErrorType {
        return try {
            // Simple parsing - in a real app you might use Gson or similar
            val cleanJson = errorJson.trim().removeSurrounding("{", "}")
            val errorValue = cleanJson.split(":")[1]
                .trim()
                .removeSurrounding("\"")
                .trim()
            
            val errorType = ApiErrorType.values().find { it.errorCode == errorValue } 
                ?: ApiErrorType.UNKNOWN
            
            // Log parsing attempt
            crashlyticsLogger.log("Parsed API error: $errorValue -> ${errorType.errorCode}")
            
            errorType
        } catch (e: Exception) {
            crashlyticsLogger.logError(e, "Failed to parse API error: $errorJson")
            ApiErrorType.UNKNOWN
        }
    }
    
    /**
     * Get user-friendly error message from raw error string
     */
    fun getErrorMessage(rawError: String): String {
        val errorType = parseApiError(rawError)
        return getUserFriendlyMessage(errorType)
    }
}

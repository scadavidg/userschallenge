package com.dummychallenge.utils

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource

class ErrorHandlerTest {

    @Nested
    @DisplayName("Given ErrorHandler.getErrorMessage method")
    inner class GetErrorMessageTests {

        @Test
        @DisplayName("When BODY_NOT_VALID error is passed, then should return user-friendly message")
        fun `Given BODY_NOT_VALID error, When getErrorMessage called, Then should return user-friendly message`() {
            // Given
            val errorJson = """{"error": "BODY_NOT_VALID"}"""

            // When
            val errorMessage = ErrorHandler.getErrorMessage(errorJson)

            // Then
            assert(errorMessage == "Invalid data format. Please check your input and try again.")
        }

        @Test
        @DisplayName("When RESOURCE_NOT_FOUND error is passed, then should return user-friendly message")
        fun `Given RESOURCE_NOT_FOUND error, When getErrorMessage called, Then should return user-friendly message`() {
            // Given
            val errorJson = """{"error": "RESOURCE_NOT_FOUND"}"""

            // When
            val errorMessage = ErrorHandler.getErrorMessage(errorJson)

            // Then
            assert(errorMessage == "The requested user was not found. It may have been deleted.")
        }

        @Test
        @DisplayName("When SERVER_ERROR error is passed, then should return user-friendly message")
        fun `Given SERVER_ERROR error, When getErrorMessage called, Then should return user-friendly message`() {
            // Given
            val errorJson = """{"error": "SERVER_ERROR"}"""

            // When
            val errorMessage = ErrorHandler.getErrorMessage(errorJson)

            // Then
            assert(errorMessage == "Server error occurred. Please try again later.")
        }

        @Test
        @DisplayName("When unknown error is passed, then should return unknown error message")
        fun `Given unknown error, When getErrorMessage called, Then should return unknown error message`() {
            // Given
            val errorJson = """{"error": "UNKNOWN_ERROR"}"""

            // When
            val errorMessage = ErrorHandler.getErrorMessage(errorJson)

            // Then
            assert(errorMessage == "An unexpected error occurred. Please try again.")
        }

        @Test
        @DisplayName("When invalid JSON is passed, then should return unknown error message")
        fun `Given invalid JSON, When getErrorMessage called, Then should return unknown error message`() {
            // Given
            val invalidJson = "invalid json"

            // When
            val errorMessage = ErrorHandler.getErrorMessage(invalidJson)

            // Then
            assert(errorMessage == "An unexpected error occurred. Please try again.")
        }
    }

    @Nested
    @DisplayName("Given ErrorHandler.parseApiError method")
    inner class ParseApiErrorTests {

        @ParameterizedTest
        @CsvSource(
            "'{\"error\": \"BODY_NOT_VALID\"}', 'BODY_NOT_VALID'",
            "'{\"error\": \"RESOURCE_NOT_FOUND\"}', 'RESOURCE_NOT_FOUND'",
            "'{\"error\": \"SERVER_ERROR\"}', 'SERVER_ERROR'",
            "'{\"error\": \"APP_ID_NOT_EXIST\"}', 'APP_ID_NOT_EXIST'",
            "'{\"error\": \"PARAMS_NOT_VALID\"}', 'PARAMS_NOT_VALID'"
        )
        @DisplayName("When valid error JSON is passed, then should parse correct error type")
        fun `Given valid error JSON, When parseApiError called, Then should parse correct error type`(
            errorJson: String,
            expectedErrorCode: String
        ) {
            // Given & When
            val errorType = ErrorHandler.parseApiError(errorJson)

            // Then
            assert(errorType.errorCode == expectedErrorCode)
        }

        @Test
        @DisplayName("When empty JSON is passed, then should return UNKNOWN error type")
        fun `Given empty JSON, When parseApiError called, Then should return UNKNOWN error type`() {
            // Given
            val emptyJson = "{}"

            // When
            val errorType = ErrorHandler.parseApiError(emptyJson)

            // Then
            assert(errorType == ErrorHandler.ApiErrorType.UNKNOWN)
        }

        @Test
        @DisplayName("When malformed JSON is passed, then should return UNKNOWN error type")
        fun `Given malformed JSON, When parseApiError called, Then should return UNKNOWN error type`() {
            // Given
            val malformedJson = "{error: BODY_NOT_VALID}"

            // When
            val errorType = ErrorHandler.parseApiError(malformedJson)

            // Then
            assert(errorType == ErrorHandler.ApiErrorType.UNKNOWN)
        }
    }

    @Nested
    @DisplayName("Given ErrorHandler.getUserFriendlyMessage method")
    inner class GetUserFriendlyMessageTests {

        @Test
        @DisplayName("When APP_ID_NOT_EXIST error type is passed, then should return authentication error message")
        fun `Given APP_ID_NOT_EXIST, When getUserFriendlyMessage called, Then should return authentication error message`() {
            // Given
            val errorType = ErrorHandler.ApiErrorType.APP_ID_NOT_EXIST

            // When
            val message = ErrorHandler.getUserFriendlyMessage(errorType)

            // Then
            assert(message == "Authentication error. Please contact support.")
        }

        @Test
        @DisplayName("When BODY_NOT_VALID error type is passed, then should return invalid data message")
        fun `Given BODY_NOT_VALID, When getUserFriendlyMessage called, Then should return invalid data message`() {
            // Given
            val errorType = ErrorHandler.ApiErrorType.BODY_NOT_VALID

            // When
            val message = ErrorHandler.getUserFriendlyMessage(errorType)

            // Then
            assert(message == "Invalid data format. Please check your input and try again.")
        }

        @Test
        @DisplayName("When RESOURCE_NOT_FOUND error type is passed, then should return not found message")
        fun `Given RESOURCE_NOT_FOUND, When getUserFriendlyMessage called, Then should return not found message`() {
            // Given
            val errorType = ErrorHandler.ApiErrorType.RESOURCE_NOT_FOUND

            // When
            val message = ErrorHandler.getUserFriendlyMessage(errorType)

            // Then
            assert(message == "The requested user was not found. It may have been deleted.")
        }

        @Test
        @DisplayName("When UNKNOWN error type is passed, then should return unexpected error message")
        fun `Given UNKNOWN, When getUserFriendlyMessage called, Then should return unexpected error message`() {
            // Given
            val errorType = ErrorHandler.ApiErrorType.UNKNOWN

            // When
            val message = ErrorHandler.getUserFriendlyMessage(errorType)

            // Then
            assert(message == "An unexpected error occurred. Please try again.")
        }
    }

    @Nested
    @DisplayName("Given ErrorHandler with complex error scenarios")
    inner class ComplexErrorScenariosTests {

        @Test
        @DisplayName("When error JSON with extra fields is passed, then should parse correctly")
        fun `Given error JSON with extra fields, When getErrorMessage called, Then should parse correctly`() {
            // Given
            val errorJson = """{"error": "BODY_NOT_VALID", "details": "email already used", "timestamp": "2024-01-01T00:00:00Z"}"""

            // When
            val errorMessage = ErrorHandler.getErrorMessage(errorJson)

            // Then
            assert(errorMessage == "Invalid data format. Please check your input and try again.")
        }

        @Test
        @DisplayName("When error JSON with whitespace is passed, then should parse correctly")
        fun `Given error JSON with whitespace, When getErrorMessage called, Then should parse correctly`() {
            // Given
            val errorJson = """  { "error" : "SERVER_ERROR" }  """

            // When
            val errorMessage = ErrorHandler.getErrorMessage(errorJson)

            // Then
            assert(errorMessage == "Server error occurred. Please try again later.")
        }

        @Test
        @DisplayName("When error JSON with different case is passed, then should return unknown error")
        fun `Given error JSON with different case, When getErrorMessage called, Then should return unknown error`() {
            // Given
            val errorJson = """{"error": "body_not_valid"}"""

            // When
            val errorMessage = ErrorHandler.getErrorMessage(errorJson)

            // Then
            assert(errorMessage == "An unexpected error occurred. Please try again.")
        }
    }
}

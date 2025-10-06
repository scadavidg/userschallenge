package com.dummychallenge.utils

import com.google.firebase.crashlytics.FirebaseCrashlytics
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Utilidad para logging y reporte de errores usando Firebase Crashlytics
 */
@Singleton
class CrashlyticsLogger @Inject constructor() {

    private val crashlytics = FirebaseCrashlytics.getInstance()

    /**
     * Registra un error no fatal en Crashlytics
     */
    fun logError(throwable: Throwable, message: String? = null) {
        try {
            message?.let { crashlytics.log(it) }
            crashlytics.recordException(throwable)
        } catch (e: Exception) {
            // Fallback: si Crashlytics falla, al menos loguear localmente
            android.util.Log.e("CrashlyticsLogger", "Error logging to Crashlytics", e)
        }
    }

    /**
     * Registra un mensaje de log
     */
    fun log(message: String) {
        try {
            crashlytics.log(message)
        } catch (e: Exception) {
            android.util.Log.d("CrashlyticsLogger", message)
        }
    }

    /**
     * Establece un atributo personalizado
     */
    fun setCustomKey(key: String, value: String) {
        try {
            crashlytics.setCustomKey(key, value)
        } catch (e: Exception) {
            android.util.Log.e("CrashlyticsLogger", "Error setting custom key: $key", e)
        }
    }

    /**
     * Establece un atributo personalizado con valor numérico
     */
    fun setCustomKey(key: String, value: Int) {
        try {
            crashlytics.setCustomKey(key, value)
        } catch (e: Exception) {
            android.util.Log.e("CrashlyticsLogger", "Error setting custom key: $key", e)
        }
    }

    /**
     * Establece un atributo personalizado con valor booleano
     */
    fun setCustomKey(key: String, value: Boolean) {
        try {
            crashlytics.setCustomKey(key, value)
        } catch (e: Exception) {
            android.util.Log.e("CrashlyticsLogger", "Error setting custom key: $key", e)
        }
    }

    /**
     * Establece el ID de usuario para el seguimiento
     */
    fun setUserId(userId: String?) {
        try {
            crashlytics.setUserId(userId ?: "anonymous")
        } catch (e: Exception) {
            android.util.Log.e("CrashlyticsLogger", "Error setting user ID", e)
        }
    }

    /**
     * Registra información del usuario
     */
    fun logUserInfo(userId: String, email: String? = null, name: String? = null) {
        try {
            setUserId(userId)
            email?.let { setCustomKey("user_email", it) }
            name?.let { setCustomKey("user_name", it) }
            log("User logged in: $userId")
        } catch (e: Exception) {
            android.util.Log.e("CrashlyticsLogger", "Error logging user info", e)
        }
    }

    /**
     * Registra información de navegación
     */
    fun logNavigation(from: String, to: String) {
        try {
            setCustomKey("navigation_from", from)
            setCustomKey("navigation_to", to)
            log("Navigation: $from -> $to")
        } catch (e: Exception) {
            android.util.Log.e("CrashlyticsLogger", "Error logging navigation", e)
        }
    }

    /**
     * Registra información de API calls
     */
    fun logApiCall(endpoint: String, method: String, statusCode: Int, duration: Long) {
        try {
            setCustomKey("api_endpoint", endpoint)
            setCustomKey("api_method", method)
            setCustomKey("api_status_code", statusCode)
            setCustomKey("api_duration_ms", duration.toString())
            log("API Call: $method $endpoint - $statusCode (${duration}ms)")
        } catch (e: Exception) {
            android.util.Log.e("CrashlyticsLogger", "Error logging API call", e)
        }
    }

    /**
     * Registra errores de validación de formularios
     */
    fun logFormValidationError(formName: String, field: String, error: String) {
        try {
            setCustomKey("form_name", formName)
            setCustomKey("validation_field", field)
            setCustomKey("validation_error", error)
            log("Form validation error in $formName.$field: $error")
        } catch (e: Exception) {
            android.util.Log.e("CrashlyticsLogger", "Error logging form validation", e)
        }
    }

    /**
     * Registra errores de red
     */
    fun logNetworkError(endpoint: String, error: String, statusCode: Int? = null) {
        try {
            setCustomKey("network_endpoint", endpoint)
            setCustomKey("network_error", error)
            statusCode?.let { setCustomKey("network_status_code", it) }
            log("Network error: $endpoint - $error")
        } catch (e: Exception) {
            android.util.Log.e("CrashlyticsLogger", "Error logging network error", e)
        }
    }

    /**
     * Registra información de rendimiento
     */
    fun logPerformance(operation: String, duration: Long, success: Boolean) {
        try {
            setCustomKey("operation", operation)
            setCustomKey("duration_ms", duration.toString())
            setCustomKey("success", success)
            log("Performance: $operation took ${duration}ms (success: $success)")
        } catch (e: Exception) {
            android.util.Log.e("CrashlyticsLogger", "Error logging performance", e)
        }
    }
}

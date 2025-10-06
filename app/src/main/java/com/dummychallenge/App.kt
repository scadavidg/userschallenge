package com.dummychallenge

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class that initializes Hilt dependency injection and Firebase.
 * @HiltAndroidApp annotation generates the DI components for the entire app.
 */
@HiltAndroidApp
class App : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
    }
}
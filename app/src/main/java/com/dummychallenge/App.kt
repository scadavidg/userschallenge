package com.dummychallenge

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class that initializes Hilt dependency injection.
 * @HiltAndroidApp annotation generates the DI components for the entire app.
 */
@HiltAndroidApp
class App : Application()
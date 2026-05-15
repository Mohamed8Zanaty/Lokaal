package com.example.lokaal

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import org.osmdroid.config.Configuration

@HiltAndroidApp
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Configuration.getInstance().apply {
            load(this@MyApp, getSharedPreferences("osmdroid", MODE_PRIVATE))
            userAgentValue = packageName
        }
    }
}
package com.govAtizapan.beneficiojoven

import android.app.Application
import android.util.Log
import com.google.android.libraries.places.api.Places
import com.govAtizapan.beneficiojoven.model.sessionManager.SessionManager

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            SessionManager.init(this)
            // Replace "YOUR_API_KEY_HERE" with your actual Google Cloud API key
            Places.initialize(applicationContext, "AIzaSyBip5hzRg22vBXTV78rJ2u9oVi_cD_I4B8")
            Log.d("MyApplication", "Places API initialized successfully")
        } catch (e: Exception) {
            Log.e("MyApplication", "Failed to initialize Places API: ${e.message}")
        }
    }
}
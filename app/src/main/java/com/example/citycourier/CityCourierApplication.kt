package com.example.citycourier

import android.app.Application
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize

class CityCourierApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Firebase.initialize(this)
    }
}
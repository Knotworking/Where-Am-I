package com.knotworking.whereami

import android.app.Application
import com.google.android.gms.maps.MapsInitializer
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class WhereAmIApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MapsInitializer.initialize(
            this,
            MapsInitializer.Renderer.LATEST,
            null
        )
    }
}

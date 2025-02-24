package com.fitformar

import android.app.Application
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Session
import com.google.ar.core.exceptions.UnavailableException

class FitFormARApplication : Application() {
    private var arCoreSession: Session? = null

    override fun onCreate() {
        super.onCreate()
        
        // Check AR availability
        when (ArCoreApk.getInstance().checkAvailability(this)) {
            ArCoreApk.Availability.SUPPORTED_INSTALLED -> {
                // AR Core is supported and installed
                try {
                    arCoreSession = Session(this)
                } catch (e: UnavailableException) {
                    // Handle exception
                }
            }
            ArCoreApk.Availability.SUPPORTED_NOT_INSTALLED -> {
                // Prompt user to install AR Core
            }
            else -> {
                // AR is not supported
            }
        }
    }

    fun getARSession(): Session? = arCoreSession
}

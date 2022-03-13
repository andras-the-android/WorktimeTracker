package hu.kts.wtracker

import android.app.Application

class WTrackerApp: Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: Application
    }
}
package de.infoware.smsparser.application

import android.app.Application
import de.infoware.android.mti.extension.MTIHelper

class MapTrip112Application : Application() {
    override fun onCreate() {
        super.onCreate()
        MTIHelper.initialize(applicationContext)
    }
}
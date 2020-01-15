package de.infoware.smsparser.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import de.infoware.smsparser.R

/**
 * Gets intents, when device is booted and starts the app
 */
class BroadcastTetraServiceStarter : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == context.getString(R.string.boot_competed_intent_action)) {
            launchTetraSmsApp(context)
        }
    }

    private fun launchTetraSmsApp(context: Context) {
        val pm = context.packageManager
        val launchIntent =
            pm.getLaunchIntentForPackage(context.getString(R.string.tetra_sms_app_package))
        context.startActivity(launchIntent)
    }
}
package de.infoware.smsparser.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.jakewharton.rxrelay2.PublishRelay
import de.infoware.smsparser.R

/**
 * Broadcast receiver, which receives messages about failures while opening TETRA Session.
 * We register this BroadcastReceiver as a context receiver from TetraMainFragment in order
 * to be able to smoothly close the app if required.
 */
class BroadcastPortFailureReceiver : BroadcastReceiver() {

    val errorRelay = PublishRelay.create<String>()

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent!!.action == context.getString(R.string.session_start_failure_intent_action)) {
            errorRelay.accept(
                intent.getStringExtra(context.getString(R.string.port_failure_message_key)))
        }
    }
}
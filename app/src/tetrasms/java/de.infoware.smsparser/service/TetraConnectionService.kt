package de.infoware.smsparser.service

import android.app.Notification
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import de.infoware.smsparser.R
import de.infoware.smsparser.tetra.TetraRawPortClient
import de.infoware.smsparser.tetra.TetraSessionListener
import de.infoware.smsparser.ui.TetraMainActivity
import io.reactivex.disposables.CompositeDisposable


/**
 * Android Service which runs in Background and observes new messages,
 * arriving through TETRA Serial Port.
 */
class TetraConnectionService : Service(), TetraSessionListener {

    private var serviceDisposables: CompositeDisposable = CompositeDisposable()

    private lateinit var chatClient: TetraRawPortClient

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        chatClient = TetraRawPortClient()
        chatClient.startSession(applicationContext, this)

        startForeground(1, Notification())

        /*
         * When user blocks screen, something strong happens with garmin device.
         * To overcome, restart the app, when screen is active again.
         */
        val screenStateFilter = IntentFilter()
        screenStateFilter.addAction(Intent.ACTION_SCREEN_ON)
        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent?) {
                stopSelf()
                val launchIntent = Intent(context, TetraMainActivity::class.java)
                launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(launchIntent)
            }

        }, screenStateFilter)

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun handleSessionOpenException(ex: Exception) {
        val localIntent = Intent(this.getString(R.string.session_start_failure_intent_action))
            .putExtra(this.getString(R.string.port_failure_message_key), ex.message)
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent)
        stopSelf()
    }

    override fun sessionStarted() {
        serviceDisposables.add(
            chatClient
                .getNewMessageObservable()
                .subscribe { message ->
                    val messageIntent = Intent(this.getString(R.string.new_message_intent_action))
                        .putExtra(this.getString(R.string.message_body_key), message)
                    sendBroadcast(messageIntent)
                }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        chatClient.stopSession()
        serviceDisposables.clear()
    }
}

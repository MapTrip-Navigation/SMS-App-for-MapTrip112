package de.infoware.smsparser.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import de.infoware.smsparser.R
import de.infoware.smsparser.tetra.TetraRawPortClient
import de.infoware.smsparser.tetra.TetraSessionListener
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

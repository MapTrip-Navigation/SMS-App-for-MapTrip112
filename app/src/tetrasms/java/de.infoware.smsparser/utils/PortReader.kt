package de.infoware.smsparser.utils

import android.os.Handler
import android.util.Log
import com.jakewharton.rxrelay2.PublishRelay
import java.io.BufferedReader

/**
 * Class which performs actual listening of the Garmin Seial Port.
 */
class PortReader(
    private val handler: Handler,
    private val inputReader: BufferedReader
) : Runnable {

    val newMessageObservable = PublishRelay.create<String>()

    override fun run() {
        try {
            val messageBody = inputReader.readLine()
            if (messageBody != null && messageBody.isNotEmpty()) {
                newMessageObservable.accept(messageBody)
            }
        } catch (ex: Exception) {
            Log.e(TAG, "Failure reading inbound message", ex)
        }
        handler.post(this)
    }

    companion object {
        private val TAG = PortReader::class.java.name
    }
}
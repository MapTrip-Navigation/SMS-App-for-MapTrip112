package de.infoware.smsparser.tetra

import android.content.Context

import com.garmin.android.comm.PortInUseException
import com.garmin.android.comm.PortNotAvailableException

import java.io.IOException
import java.util.concurrent.TimeoutException

/**
 * Interface defines principle functioning of classes, which implement communication through TETRA.
 */
interface TetraClient {
    @Throws(
        PortInUseException::class,
        PortNotAvailableException::class,
        IOException::class,
        TimeoutException::class
    )
    fun startSession(context: Context, sessionListener: TetraSessionListener)

    @Throws(IOException::class)
    fun stopSession()

    fun sessionStarted(sessionListener: TetraSessionListener)
}

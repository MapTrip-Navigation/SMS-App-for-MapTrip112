package de.infoware.smsparser.tetra

import android.content.Context
import android.os.Handler
import android.os.HandlerThread

import com.garmin.android.comm.SerialPort
import de.infoware.smsparser.utils.PortReader
import io.reactivex.Observable

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * Implementation of TETRA client using [SerialPort.SERIAL_PORT_RAW_0].
 */
class TetraRawPortClient : TetraClient {

    private lateinit var serialPort: SerialPort
    private lateinit var inputReader: BufferedReader

    private lateinit var readChecker: PortReader

    override fun sessionStarted(sessionListener: TetraSessionListener) {
        try {
            inputReader = BufferedReader(
                InputStreamReader(
                    serialPort.inputStream,
                    UTF_8_CHAR_SET
                )
            )
            readChecker = PortReader(readHandler, inputReader)
            readHandler.post(readChecker)
        } catch (ex: IOException) {
            sessionListener.handleSessionOpenException(ex)
        }
    }

    fun getNewMessageObservable(): Observable<String> {
        return readChecker.newMessageObservable
    }

    private var serialPortIsOpen: Boolean = false

    // thread for blocking IO read from serial port
    private lateinit var handlerThread: HandlerThread

    private lateinit var readHandler: Handler

    override fun startSession(context: Context, sessionListener: TetraSessionListener) {
        handlerThread = HandlerThread("Tetra serial port handler")
        handlerThread.start()
        readHandler = Handler(handlerThread.looper)
        readHandler.post {
            try {
                serialPort = SerialPort.getSerialPort(context, SerialPort.SERIAL_PORT_RAW_0)

                serialPort.open(
                    portOwnerName,
                    CHAT_CLIENT_SERIAL_PORT_OPEN_TIMEOUT, null,
                    SerialPort.OPEN_OPTIONS_NONE
                )

                serialPort.setConfiguration(
                    SerialPort.BaudRate.BAUD_RATE_38400,
                    SerialPort.DataBits.DATA_BITS_8,
                    SerialPort.Parity.PARITY_NONE,
                    SerialPort.StopBits.STOP_BITS_1
                )

                serialPortIsOpen = true
                sessionStarted(sessionListener)
                sessionListener.sessionStarted()

            } catch (ex: Exception) {
                sessionListener.handleSessionOpenException(ex)
            }
        }
    }

    @Throws(IOException::class)
    override fun stopSession() {
        handlerThread.quit()

        if (serialPortIsOpen) {
            serialPort.close()
            serialPortIsOpen = false
        }
    }

    companion object {
        private const val UTF_8_CHAR_SET = "UTF-8"

        /**
         * timeout in milliseconds, according to [com.garmin.android.comm.SerialPort.open]
         */
        private const val CHAT_CLIENT_SERIAL_PORT_OPEN_TIMEOUT = 10000

        private const val portOwnerName = "MAPTRIP 112"
    }

}

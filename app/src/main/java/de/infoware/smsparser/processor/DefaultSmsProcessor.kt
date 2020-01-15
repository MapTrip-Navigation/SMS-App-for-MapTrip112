package de.infoware.smsparser.processor

import de.infoware.smsparser.data.DestinationInfo
import io.reactivex.Single
import java.util.Calendar

/**
 * Process messages in the following format:
 * "51.2123544, 6.12548543;Zimmerbrand Musterstrasse 26 3:OG
 */
class DefaultSmsProcessor : SmsProcessor() {

    companion object {
        const val symbolMessagePartDelimiter = ';'
        const val symbolCoordinatePartDelimiter = ','
    }

    private val calendar: Calendar = Calendar.getInstance()

    override fun execute(param: String?): Single<DestinationInfo> {
        return Single.create { emitter ->
            val lat: Double
            val lon: Double
            val reason: String

            val messageParts = param?.split(symbolMessagePartDelimiter)
            if (messageParts?.size == 2) {
                val coordinate = messageParts.first()
                    .replace(" ", "").split(symbolCoordinatePartDelimiter)
                if (coordinate.size == 2) {
                    try {
                        lat = coordinate.first().toDouble()
                        lon = coordinate.last().toDouble()
                        reason = messageParts.last()
                        val destinationInfo =
                            DestinationInfo(lat, lon, reason, calendar.timeInMillis)
                        emitter.onSuccess(destinationInfo)
                    } catch (e: NumberFormatException) {
                        e.printStackTrace()
                        emitter.onError(IllegalArgumentException(exceptionMessage))
                    }
                    return@create
                }
            }
            emitter.onError(IllegalArgumentException(exceptionMessage))
        }
    }
}
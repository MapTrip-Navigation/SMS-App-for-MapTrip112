package de.infoware.smsparser.processor

import de.infoware.smsparser.data.DestinationInfo
import io.reactivex.Single
import java.util.*

/**
 * Process messages in the following format:
 * - 51.2123544, 6.12548543
 * - 51.2123544, 6.12548543;SoSi
 * - 51.2123544, 6.12548543;Zimmerbrand Musterstrasse 26 3:OG
 * - 51.2123544, 6.12548543;SoSi;Zimmerbrand Musterstrasse 26 3:OG
 */
class DefaultSmsProcessor : SmsProcessor() {
    override fun extractBlueLightInfo(messageParts: List<String>): Boolean {
        var blueLightNavigation = false
        when (messageParts.size) {
            2 -> blueLightNavigation = messageParts.last() == symbolSoSi
            3 -> blueLightNavigation = messageParts[1] == symbolSoSi
        }
        return blueLightNavigation
    }

    override fun extractReasonInfo(messageParts: List<String>): String {
        var reason = ""
        when (messageParts.size) {
            1 -> reason = messageParts.first()
            2 -> reason =
                if (messageParts.last() == symbolSoSi) messageParts.first() else messageParts.last()
            3 -> reason = messageParts.last()
        }
        return reason
    }

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
            val blueLightNavigation: Boolean

            val messageParts = param?.split(symbolMessagePartDelimiter)
            if (messageParts != null) {
                val coordinate = messageParts.first()
                    .replace(" ", "").split(symbolCoordinatePartDelimiter)
                if (coordinate.size == 2) {
                    try {
                        lat = coordinate.first().toDouble()
                        lon = coordinate.last().toDouble()
                        reason = extractReasonInfo(messageParts)
                        blueLightNavigation = extractBlueLightInfo(messageParts)
                        val destinationInfo =
                            DestinationInfo(
                                lat,
                                lon,
                                reason,
                                calendar.timeInMillis,
                                blueLightNavigation
                            )
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
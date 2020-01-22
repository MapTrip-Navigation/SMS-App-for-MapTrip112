package de.infoware.smsparser.processor

import de.infoware.smsparser.data.DestinationInfo
import io.reactivex.Single
import java.util.*

/**
 * Class provides parsing for messages in the following format:
 * - #K01;N{LAT}E{LONG} without decimal point.
 * After parsing, representation contains always 5 digits after decimal point.
 *
 * Messages may also have parts, corresponding to SoSi - Sondernsignal (start blue light navigation)
 * and free text part:
 * - #K01;N{LAT}E{LONG};SoSi - Sondersignal without free text;
 * - #K01;N{LAT}E{LONG};Zimmerbrand - Free text without Sondersignal;
 * - #K01;N{LAT}E{LONG};SoSi;Zimmerbrand - Both Sondresignal and Free text info.
 */
class TetraK01Processor : SmsProcessor() {
    companion object {
        const val decimalPointOffsetFromEnd = 5
        const val symbolDecimalPoint = '.'

        const val symbolN = 'N'
        const val symbolE = 'E'

        const val symbolDelimiter = ';'
    }

    override fun extractReasonInfo(messageParts: List<String>): String {
        var reason = ""
        when (messageParts.size) {
            2 -> reason = messageParts.first()
            3 -> reason =
                if (messageParts.last() == symbolSoSi) messageParts.first() else messageParts.last()
            4 -> reason = messageParts.last()
        }

        return reason
    }

    override fun extractBlueLightInfo(messageParts: List<String>): Boolean {
        var blueLightNavigation = false
        when (messageParts.size) {
            2 -> blueLightNavigation = false
            3 -> blueLightNavigation = messageParts.last() == symbolSoSi
            4 -> blueLightNavigation = messageParts[2] == symbolSoSi
        }
        return blueLightNavigation
    }

    private val calendar: Calendar = Calendar.getInstance()

    override fun execute(param: String?): Single<DestinationInfo> {

        return Single.create { emitter ->
            val lat: Double
            val lon: Double
            val reason: String
            val blueLightNavigation: Boolean

            val messageParts = param?.split(symbolDelimiter)
            if (messageParts != null && messageParts.size >= 2) {
                val coordinates = messageParts[1]
                val indexOfN = coordinates.indexOf(symbolN)
                val indexOfE = coordinates.indexOf(symbolE)
                if (indexOfE != -1 && indexOfN != -1) {
                    try {
                        val latStr = StringBuilder(coordinates.substring(indexOfN + 1, indexOfE))
                        val lonStr =
                            StringBuilder(coordinates.substring(indexOfE + 1, coordinates.length))

                        latStr.insert(
                            latStr.length - decimalPointOffsetFromEnd, symbolDecimalPoint
                        )
                        lonStr.insert(
                            lonStr.length - decimalPointOffsetFromEnd, symbolDecimalPoint
                        )

                        lat = latStr.toString().toDouble()
                        lon = lonStr.toString().toDouble()

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
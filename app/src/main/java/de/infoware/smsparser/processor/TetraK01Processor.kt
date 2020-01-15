package de.infoware.smsparser.processor

import de.infoware.smsparser.data.DestinationInfo
import io.reactivex.Single
import java.util.Calendar

/**
 * Class provides parsing for messages in the following format:
 * #K01;N{LAT}E{LONG} without decimal point.
 * After parsing, representation contains always 5 digits after decimal point.
 */
class TetraK01Processor : SmsProcessor() {

    companion object {
        const val decimalPointOffsetFromEnd = 5
        const val symbolDecimalPoint = '.'

        const val symbolN = 'N'
        const val symbolE = 'E'

        const val symbolDelimiter = ';'
    }

    private val calendar: Calendar = Calendar.getInstance()

    override fun execute(param: String?): Single<DestinationInfo> {

        return Single.create { emitter ->
            val lat: Double
            val lon: Double
            val reason: String

            val messageParts = param?.split(symbolDelimiter)
            if (messageParts?.size == 2) {
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

                        reason = messageParts.first()
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
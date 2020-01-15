package de.infoware.smsparser.processor

import de.infoware.smsparser.data.DestinationInfo
import io.reactivex.Single
import java.util.Calendar

/**
 * Class provides parsing for messages in the following format:
 * TVPN{LAT}E{LONG} without decimal point.
 * Moreover coordinates are initially encoded with Hexadecimal representation with 8 digits.
 * After parsing, representation contains always 6 digits after decimal point.
 */
class TetraTVPNProcessor : SmsProcessor() {

    companion object {
        const val decimalPointOffsetFromEnd = 6
        const val symbolDecimalPoint = '.'

        const val symbolN = 'N'
        const val symbolE = 'E'

        const val radix = 16
    }

    private val calendar: Calendar = Calendar.getInstance()

    override fun execute(param: String?): Single<DestinationInfo> {

        return Single.create { emitter ->
            val lat: Double
            val lon: Double
            val reason: String

            if (param == null) {
                emitter.onError(IllegalArgumentException(exceptionMessage))
                return@create
            }
            val indexOfN = param.indexOf(symbolN)
            if (indexOfN != -1) {
                reason = param.substring(0, indexOfN + 1)
                val indexOfE = param.indexOf(symbolE)
                if (indexOfE != -1 && indexOfN != -1) {
                    try {
                        val latStrHex = param.substring(indexOfN + 1, indexOfE)
                        val lonStrHex = param.substring(indexOfE + 1, param.length)

                        val latStr = StringBuilder(Integer.parseInt(latStrHex, radix).toString())
                        val lonStr = StringBuilder(Integer.parseInt(lonStrHex, radix).toString())

                        latStr.insert(
                            latStr.length - decimalPointOffsetFromEnd, symbolDecimalPoint
                        )
                        lonStr.insert(
                            lonStr.length - decimalPointOffsetFromEnd, symbolDecimalPoint
                        )

                        lat = latStr.toString().toDouble()
                        lon = lonStr.toString().toDouble()

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
package de.infoware.smsparser.processor

import de.infoware.smsparser.data.DestinationInfo
import io.reactivex.Single
import java.util.*

/**
 * Class provides parsing for messages in the following format:
 * TVPN{LAT}E{LONG} without decimal point.
 * Moreover coordinates are initially encoded with Hexadecimal representation with 8 digits.
 * After parsing, representation contains always 6 digits after decimal point.
 *
 * Messages may also have parts, corresponding to SoSi - Sondernsignal (start blue light navigation)
 * and free text part:
 * - TVPN{LAT}E{LONG};SoSi - Sondersignal without free text;
 * - TVPN{LAT}E{LONG};Zimmerbrand - Free text without Sondersignal;
 * - TVPN{LAT}E{LONG};SoSi;Zimmerbrand - Both Sondresignal and Free text info.
 */
class TetraTVPNProcessor : SmsProcessor() {

    override fun extractReasonInfo(messageParts: List<String>): String {
        var reason = ""
        when (messageParts.size) {
            2 -> reason =
                if (messageParts.last() == symbolSoSi) "" else messageParts.last()
            3 -> reason = messageParts.last()
        }
        return reason
    }

    override fun extractBlueLightInfo(messageParts: List<String>): Boolean {
        var blueLightNavigation = false
        when (messageParts.size) {
            2 -> blueLightNavigation =
                messageParts.last() == symbolSoSi
            3 -> blueLightNavigation = messageParts[1] == symbolSoSi
        }
        return blueLightNavigation
    }

    companion object {
        const val decimalPointOffsetFromEnd = 6
        const val symbolDecimalPoint = '.'

        const val symbolN = 'N'
        const val symbolE = 'E'

        const val symbolDelimiter = ';'

        const val radix = 16
    }

    private val calendar: Calendar = Calendar.getInstance()

    override fun execute(param: String?): Single<DestinationInfo> {

        return Single.create { emitter ->
            val lat: Double
            val lon: Double
            var reason: String
            var blueLightNavigation = false
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
                        val lonStrHex = param.substring(indexOfE + 1, indexOfE + 1 + 8)

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

                        val messageParts = param.split(symbolDelimiter)
                        if (messageParts.size > 1) {
                            val reasonCandidate = extractReasonInfo(messageParts)
                            reason = if (reasonCandidate.isEmpty()) reason else reasonCandidate
                            blueLightNavigation = extractBlueLightInfo(messageParts)
                        }

                        val destinationInfo =
                            DestinationInfo(
                                lat, lon, reason,
                                calendar.timeInMillis, blueLightNavigation
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
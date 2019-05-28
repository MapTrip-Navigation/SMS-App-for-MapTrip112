package de.infoware.smsparser.processor

import de.infoware.smsparser.data.DestinationInfo
import io.reactivex.Single
import java.util.*

class DefaultSmsProcessor : SmsProcessor {

    private val exceptionMessage = "Sms had incorrect structure"
    private val calendar = Calendar.getInstance()

    override fun execute(param: String?): Single<DestinationInfo> {
        val lat: Double
        val lon: Double
        val reason: String

        val messageParts = param?.split(";")
        if (messageParts?.size == 2) {
            val coordinate = messageParts.first().replace(" ", "").split(",")
            if (coordinate.size == 2) {
                try {
                    lat = coordinate.first().toDouble()
                    lon = coordinate.last().toDouble()
                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                    return Single.error { IllegalArgumentException(exceptionMessage) }
                }
                reason = messageParts.last()
                val destinationInfo = DestinationInfo(lat, lon, reason, calendar.timeInMillis)
                return Single.just(destinationInfo)
            }
        }
        return Single.error { IllegalArgumentException(exceptionMessage) }
    }

}
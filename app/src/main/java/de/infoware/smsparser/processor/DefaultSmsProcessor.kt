package de.infoware.smsparser.processor

import de.infoware.smsparser.DestinationInfo
import io.reactivex.Single
import java.util.*

class DefaultSmsProcessor() : SmsProcessor {

    private val calendar = Calendar.getInstance()

    override fun execute(param: String?): Single<DestinationInfo> {
        var destinationInfo = DestinationInfo(0.0, 0.0, "", 0L, false)

        var lat = 0.0
        var lon = 0.0
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
                }
            }
            reason = messageParts.last()
            destinationInfo = DestinationInfo(lat, lon, reason, calendar.timeInMillis)

        }
        return Single.just(destinationInfo)
    }

}
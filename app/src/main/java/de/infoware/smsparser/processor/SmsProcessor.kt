package de.infoware.smsparser.processor

import de.infoware.smsparser.data.DestinationInfo
import io.reactivex.Single

abstract class SmsProcessor {

    companion object {
        const val exceptionMessage = "Sms had incorrect structure"
        const val symbolSoSi = "SoSi"
    }

    abstract fun execute(param: String?): Single<DestinationInfo>

    abstract fun extractBlueLightInfo(messageParts: List<String>): Boolean

    abstract fun extractReasonInfo(messageParts: List<String>): String
}
package de.infoware.smsparser.processor

import de.infoware.smsparser.data.DestinationInfo
import io.reactivex.Single
import java.util.*

abstract class SmsProcessor {

    companion object {
        const val exceptionMessage = "Sms had incorrect structure"
    }

    abstract fun execute(param: String?): Single<DestinationInfo>
}
package de.infoware.smsparser.processor

import de.infoware.smsparser.data.DestinationInfo
import io.reactivex.Single

interface SmsProcessor {
    fun execute(param: String?): Single<DestinationInfo>
}
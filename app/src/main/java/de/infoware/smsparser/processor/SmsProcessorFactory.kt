package de.infoware.smsparser.processor

abstract class SmsProcessorFactory {

    private val stub = ""
    fun getSmsProcessor(phoneNumber: String): SmsProcessor {
        return when (phoneNumber) {
            stub -> DefaultSmsProcessor()

            else -> DefaultSmsProcessor()
        }
    }
}
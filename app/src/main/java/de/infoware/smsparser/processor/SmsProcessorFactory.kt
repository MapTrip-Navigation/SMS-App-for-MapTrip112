package de.infoware.smsparser.processor

abstract class SmsProcessorFactory {

    companion object {
        private val stub = "-1"
        fun getSmsProcessor(phoneNumber: String): SmsProcessor {
            return when (phoneNumber) {
                stub -> DefaultSmsProcessor()

                else -> DefaultSmsProcessor()
            }
        }
    }

}
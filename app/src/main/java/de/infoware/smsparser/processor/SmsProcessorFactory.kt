package de.infoware.smsparser.processor

/**
 * Abstract factory for choosing the right SmsProcessor.
 * Corresponding processor is defined according to the phone number, which was used for sending SMS.
 */
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
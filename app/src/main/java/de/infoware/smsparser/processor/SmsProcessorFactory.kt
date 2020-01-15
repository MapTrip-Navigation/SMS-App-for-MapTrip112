package de.infoware.smsparser.processor

import de.infoware.smsparser.processor.util.STANDARD_SMS
import de.infoware.smsparser.processor.util.TETRA_K01
import de.infoware.smsparser.processor.util.TETRA_TVPN

/**
 * Abstract factory for choosing the right SmsProcessor.
 * Corresponding processor is defined according to the phone number, which was used for sending SMS.
 */
abstract class SmsProcessorFactory {
    companion object {
        fun getSmsProcessor(messageSender: String): SmsProcessor {
            return when (messageSender) {
                STANDARD_SMS -> DefaultSmsProcessor()
                TETRA_K01 -> TetraK01Processor()
                TETRA_TVPN -> TetraTVPNProcessor()
                else -> DefaultSmsProcessor()
            }
        }
    }

}
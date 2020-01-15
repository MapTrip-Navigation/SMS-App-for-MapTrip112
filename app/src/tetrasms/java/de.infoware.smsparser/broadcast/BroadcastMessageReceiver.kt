package de.infoware.smsparser.broadcast

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import de.infoware.smsparser.R
import de.infoware.smsparser.data.storage.DestinationDatabase
import de.infoware.smsparser.domain.DestinationSaver
import de.infoware.smsparser.message.SmsInfo
import de.infoware.smsparser.processor.SmsProcessorFactory
import de.infoware.smsparser.processor.util.TETRA_K01
import de.infoware.smsparser.processor.util.TETRA_K01_PREFIX
import de.infoware.smsparser.processor.util.TETRA_TVPN
import de.infoware.smsparser.processor.util.TETRA_TVPN_PREFIX
import de.infoware.smsparser.repository.LocalDestinationRepository
import de.infoware.smsparser.service.TetraConnectionService
import de.infoware.smsparser.ui.TetraMainActivity


/**
 * Broadcast receiver for incoming messages.
 * This receiver is triggered when [TetraConnectionService] receives a new message and
 * send a broadcast.
 */
class BroadcastMessageReceiver : BroadcastReceiver() {

    companion object {
        val STUB_SMS_INFO = SmsInfo("", "")
        const val UTILITY_PREFIX_LENGTH = 8
    }

    @SuppressLint("CheckResult")
    override fun onReceive(context: Context, intent: Intent) {
        val message = getSmsInfoFromIntent(intent, context)
        if (message.smsBody != "") {
            SmsProcessorFactory.getSmsProcessor(message.smsSender)
                .execute(message.smsBody)
                .flatMapCompletable {
                    DestinationSaver(
                        LocalDestinationRepository(
                            DestinationDatabase.getInstance(
                                context
                            )
                        )
                    )
                        .execute(it)
                }
                .subscribe(
                    { launchSmsApp(context) },
                    { e -> e.printStackTrace() }
                )
        }
    }

    @Suppress("DEPRECATION")
    private fun getSmsInfoFromIntent(intent: Intent, context: Context): SmsInfo {
        var smsBody = ""
        var smsSender = ""
        val messageBodyKey = context.getString(R.string.message_body_key)

        if (intent.action == context.getString(R.string.new_message_intent_action)) {
            // We do not parse the utility information from TETRA, so just skip
            if (intent.getStringExtra(messageBodyKey).startsWith(context.getString(R.string.tetra_utility_message_prefix))) {
                return STUB_SMS_INFO
            }
            val receivedHex = intent.getStringExtra(messageBodyKey)
            smsBody =
                smsBody.plus(
                    convertHexToCharString(
                        receivedHex?.substring(UTILITY_PREFIX_LENGTH, receivedHex.length)
                    )
                )

            // Possible message formats
            if (smsBody.startsWith(TETRA_K01_PREFIX)) {
                smsSender = TETRA_K01
            }

            if (smsBody.startsWith(TETRA_TVPN_PREFIX)) {
                smsSender = TETRA_TVPN
            }
        }
        return SmsInfo(smsBody, smsSender)
    }

    /**
     * Performs conversion from Hex encoding text to UTF-8
     */
    private fun convertHexToCharString(hexString: String?): String {
        if (hexString == null) {
            return ""
        }
        val output = StringBuilder("")
        var i = 0
        while (i < hexString.length) {
            val str = hexString.substring(i, i + 2)
            output.append(Integer.parseInt(str, 16).toChar())
            i += 2
        }
        return output.toString()
    }

    /**
     * We have to change this method in comparison with standardsms flavour because of the issue
     * related to restarting device.
     * In this implementation we can track messages directly after restart.
     */
    private fun launchSmsApp(context: Context) {
        val launchIntent = Intent(context, TetraMainActivity::class.java)
        launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(launchIntent)
    }
}
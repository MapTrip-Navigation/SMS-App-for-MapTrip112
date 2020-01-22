package de.infoware.smsparser.broadcast

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Telephony
import android.telephony.SmsMessage
import de.infoware.smsparser.data.storage.DestinationDatabase
import de.infoware.smsparser.domain.DestinationSaver
import de.infoware.smsparser.message.SmsInfo
import de.infoware.smsparser.processor.SmsProcessorFactory
import de.infoware.smsparser.processor.util.TETRA_K01
import de.infoware.smsparser.processor.util.TETRA_K01_PREFIX
import de.infoware.smsparser.processor.util.TETRA_TVPN
import de.infoware.smsparser.processor.util.TETRA_TVPN_PREFIX
import de.infoware.smsparser.repository.LocalDestinationRepository

/**
 * Broadcast receiver for parsing sms.
 */
class SmsBroadcastReceiver : BroadcastReceiver() {
    companion object {
        const val SMS_PARSER_APP_PACKAGE = "de.infoware.smsparser.standardsms"
        const val SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED"
    }


    @SuppressLint("CheckResult")
    override fun onReceive(context: Context, intent: Intent?) {
        val message = getSmsInfoFromIntent(intent)
        SmsProcessorFactory.getSmsProcessor(message.smsSender)
            .execute(message.smsBody)
            .flatMapCompletable {
                DestinationSaver(LocalDestinationRepository(DestinationDatabase.getInstance(context)))
                    .execute(it)
            }
            .subscribe(
                { launchSmsApp(context) },
                { e -> e.printStackTrace() }
            )
    }

    @Suppress("DEPRECATION")
    private fun getSmsInfoFromIntent(intent: Intent?): SmsInfo {
        var smsBody = ""
        var smsSender = ""
        if (intent?.action == SMS_RECEIVED_ACTION) {
            val bundle = intent.extras
            val messages: Array<SmsMessage?>
            if (bundle != null) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    val pdus = bundle.get("pdus") as Array<*>
                    messages = arrayOfNulls(pdus.size)
                    for (i in 0 until pdus.size) {
                        messages[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray)
                    }
                } else {
                    messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
                }
                for (message in messages) {
                    smsBody = smsBody.plus(message?.displayMessageBody)
                    // Possible message formats
                    if (smsBody.startsWith(TETRA_K01_PREFIX)) {
                        smsSender = TETRA_K01
                    } else if (smsBody.startsWith(TETRA_TVPN_PREFIX)) {
                        smsSender = TETRA_TVPN
                    } else {
                        smsSender = smsSender.plus(message?.displayOriginatingAddress)
                    }
                    break
                }
            }
        }
        return SmsInfo(smsBody, smsSender)
    }


    private fun launchSmsApp(context: Context) {
        val pm = context.packageManager
        val launchIntent = pm.getLaunchIntentForPackage(SMS_PARSER_APP_PACKAGE)
        context.startActivity(launchIntent)
    }

}
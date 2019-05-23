package de.infoware.smsparser.broadcast

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Telephony
import android.telephony.SmsMessage
import de.infoware.smsparser.SmsInfo
import de.infoware.smsparser.domain.DestinationSaver
import de.infoware.smsparser.processor.SmsProcessorFactory
import de.infoware.smsparser.repository.LocalDestinationRepository
import de.infoware.smsparser.storage.DestinationDatabase


class SmsBroadcastReceiver : BroadcastReceiver() {
    companion object {
        const val SMS_PARSER_APP_PACKAGE = "de.infoware.smsparser"
        const val SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED"
//        const val DUMMY = "51.2123544, 6.12548543;Zimmerbrand Musterstrasse 26 3:OG"
    }


    @SuppressLint("CheckResult")
    override fun onReceive(context: Context, intent: Intent?) {
        val message = getSmsInfoFromIntent(intent)
        SmsProcessorFactory.getSmsProcessor(message.smsSender)
            .execute(message.smsBody)
            .doOnSuccess {
                DestinationSaver(LocalDestinationRepository(DestinationDatabase.getInstance(context)))
                    .execute(it)
                    .subscribe()
            }
            .subscribe { _ -> launchSmsApp(context) }
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
                    smsSender = smsSender.plus(message?.displayOriginatingAddress)
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
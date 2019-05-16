package de.infoware.smsparser.broadcast

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Telephony
import android.telephony.SmsMessage
import android.widget.Toast
import de.infoware.smsparser.DestinationInfo
import de.infoware.smsparser.processor.DefaultSmsProcessor

class SmsBroadcastReceiver : BroadcastReceiver() {
    companion object {
        const val SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED"
//        const val DUMMY = "51.2123544, 6.12548543;Zimmerbrand Musterstrasse 26 3:OG"
    }

    @SuppressLint("CheckResult")
    override fun onReceive(context: Context, intent: Intent?) {
        val message = getSmsBodyFromIntent(intent)
        DefaultSmsProcessor().execute(message).subscribe { waypoint ->
            launchMapTrip(context, waypoint)
        }
    }

    @Suppress("DEPRECATION")
    private fun getSmsBodyFromIntent(intent: Intent?): String {
        var smsBody = ""
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
                }
            }
        }
        return smsBody
    }


    private fun launchMapTrip(context: Context, waypoint: DestinationInfo) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(
                "maptrip://navigate?latitude=${waypoint.lat}" +
                        "&longitude=${waypoint.lon}"
            )
        )
        context.startActivity(intent)
    }

}
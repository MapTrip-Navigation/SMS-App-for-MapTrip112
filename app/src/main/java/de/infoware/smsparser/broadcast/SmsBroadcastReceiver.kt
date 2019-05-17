package de.infoware.smsparser.broadcast

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Telephony
import android.telephony.SmsMessage
import de.infoware.smsparser.DestinationInfo
import de.infoware.smsparser.SmsInfo
import de.infoware.smsparser.domain.DestinationSaver
import de.infoware.smsparser.processor.SmsProcessorFactory
import de.infoware.smsparser.storage.DestinationDatabase

class SmsBroadcastReceiver : BroadcastReceiver() {
    companion object {
        const val MAPTRIP_APP_LICENSE_PACKAGE = "de.infoware.maptrip.navi.license"
        const val SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED"
//        const val DUMMY = "51.2123544, 6.12548543;Zimmerbrand Musterstrasse 26 3:OG"
    }

    @SuppressLint("CheckResult")
    override fun onReceive(context: Context, intent: Intent?) {
        val message = getSmsInfoFromIntent(intent)
        SmsProcessorFactory.getSmsProcessor(message.smsSender)
            .execute(message.smsBody)
            .doOnSuccess {
                DestinationSaver(DestinationDatabase.getInstance(context))
                    .execute(it)
                    .subscribe()
            }
            .subscribe { waypoint ->
                launchMapTrip(context, waypoint)
            }
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


    private fun launchMapTrip(context: Context, destinationInfo: DestinationInfo) {
//        val intent = context.packageManager.getLaunchIntentForPackage(MAPTRIP_APP_LICENSE_PACKAGE)
//
//        try {
//            context.startActivity(intent)
//            Api.init()
//            Navigation.appendDestinationCoordinate(destinationInfo.lat, destinationInfo.lon)
//            Navigation.startNavigation()
//        } catch (eToo: ActivityNotFoundException) {
//            Toast.makeText(context, R.string.maptrip_start_fail, Toast.LENGTH_LONG).show()
//        }


        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(
                "maptrip://navigate?latitude=${destinationInfo.lat}" +
                        "&longitude=${destinationInfo.lon}"
            )
        )
        context.startActivity(intent)
    }

}
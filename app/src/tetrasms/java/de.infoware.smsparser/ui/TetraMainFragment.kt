@file:Suppress("DEPRECATION")

package de.infoware.smsparser.ui

import android.app.ActivityManager
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.garmin.android.comm.SerialPort
import com.jakewharton.rxrelay2.PublishRelay
import de.infoware.smsparser.R
import de.infoware.smsparser.broadcast.BroadcastPortFailureReceiver
import de.infoware.smsparser.service.TetraConnectionService
import de.infoware.smsparser.ui.presenter.MainPresenter
import de.infoware.smsparser.ui.view.MainFragment
import io.reactivex.Observable

/**
 * This Fragment extends [MainFragment], taking all the default behaviour from it and adding some
 * featurs:
 *  - Requests permission, required for [SerialPort]
 *  - Registers [BroadcastPortFailureReceiver] and listens to the port failures
 *  - Shows corresponding error dialog, if problems with port occurs.
 */
class TetraMainFragment : MainFragment(), TetraMainView {
    override fun getOnPortFailureDialogNeutralClickedOnservable(): Observable<Any> {
        return portFailureDialogNeutralClickRelay
    }

    private val portFailureDialogNeutralClickRelay = PublishRelay.create<Any>()

    override fun showPortFailureAlertDialog(errorMessage: String) {
        val builder = AlertDialog.Builder(context!!)
        builder.setMessage(getString(R.string.port_failure_dialog_message) + " " + errorMessage)
            .setTitle(R.string.attention)
        builder.setNeutralButton(R.string.ok) { _, _ ->
            portFailureDialogNeutralClickRelay.accept(Any())
        }
        val dialog = builder.create()
        dialog.show()
    }

    override fun getPortFailureObservable(): Observable<String> {
        return portFailureBroadcastReceiver.errorRelay
    }

    private val portFailureBroadcastReceiver = BroadcastPortFailureReceiver()

    override fun startTetraConnectionService() {
        if (!isMyServiceRunning(TetraConnectionService::class.java)) {
            val intent = Intent(context, TetraConnectionService::class.java)
            activity!!.startService(intent)
        }
    }

    override fun requestSerialPortPermission(requestCode: Int) {
        requestPermission(SerialPort.PERMISSION_SERIAL_PORT, requestCode)
    }

    override fun checkSerialPortPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            activity!!, SerialPort.PERMISSION_SERIAL_PORT
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun providePresenter(): MainPresenter {
        return TetraMainPresenter()
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {

        val manager = activity!!.getSystemService(ACTIVITY_SERVICE) as ActivityManager?
        for (service in manager!!.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocalBroadcastManager.getInstance(context!!).registerReceiver(
            portFailureBroadcastReceiver,
            IntentFilter(context!!.getString(R.string.session_start_failure_intent_action))
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(context!!)
            .unregisterReceiver(portFailureBroadcastReceiver)
    }
}
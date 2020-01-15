package de.infoware.smsparser.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import de.infoware.smsparser.ui.presenter.MainPresenter
import de.infoware.smsparser.ui.view.MainFragment

/**
 * This Fragment extends [MainFragment], taking all the default behaviour from it and adding some
 * featurs:
 *  - Requests permission, required for sms receiving and parsing.
 */
class StandardSmsMainFragment : MainFragment(), StandardSmsMainView {

    override fun requestReceiveSmsPermission(requestCode: Int) {
        requestPermission(Manifest.permission.RECEIVE_SMS, requestCode)
    }

    override fun requestReadSmsPermission(requestCode: Int) {
        requestPermission(Manifest.permission.READ_SMS, requestCode)
    }

    override fun checkReceiveSmsPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            activity!!, Manifest.permission.RECEIVE_SMS
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun checkReadSmsPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            activity!!, Manifest.permission.READ_SMS
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun providePresenter(): MainPresenter {
        return StandardSmsMainPresenter()
    }
}
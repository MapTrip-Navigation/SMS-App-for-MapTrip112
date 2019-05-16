package de.infoware.smsparser.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.jakewharton.rxrelay2.PublishRelay
import de.infoware.smsparser.R
import de.infoware.smsparser.permission.PermissionResult
import io.reactivex.Observable
import net.grandcentrix.thirtyinch.TiFragment

class MainFragment :
    TiFragment<MainPresenter, MainView>(),
    MainView {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        // Create the view
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun navigateToMainFragment() {
        return
    }

    override fun requestReceiveSmsPermission(requestCode: Int) {
        requestPermission(Manifest.permission.RECEIVE_SMS, requestCode)
    }

    override fun requestReadSmsPermission(requestCode: Int) {
        requestPermission(Manifest.permission.READ_SMS, requestCode)
    }

    private fun requestPermission(permission: String, requestCode: Int) {
        ActivityCompat.requestPermissions(
            activity!! as MainActivity, arrayOf(permission), requestCode
        )
    }

    override fun checkReceiveSmsPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            activity!! as MainActivity, Manifest.permission.RECEIVE_SMS
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun checkReadSmsPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            activity!! as MainActivity, Manifest.permission.READ_SMS
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun providePresenter(): MainPresenter {
        return MainPresenter()
    }


    val permissionResultRelay = PublishRelay.create<PermissionResult>()
    override fun getPermissionResultObservable(): Observable<PermissionResult> {
        return permissionResultRelay
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionResultRelay.accept(PermissionResult(requestCode, grantResults))
    }
}
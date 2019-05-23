package de.infoware.smsparser.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.jakewharton.rxrelay2.PublishRelay
import de.infoware.smsparser.R
import de.infoware.smsparser.domain.DestinationLoader
import de.infoware.smsparser.permission.PermissionResult
import de.infoware.smsparser.repository.LocalDestinationRepository
import de.infoware.smsparser.storage.DestinationDatabase
import io.reactivex.Observable
import net.grandcentrix.thirtyinch.TiFragment


class MainFragment : TiFragment<MainPresenter, MainView>(), MainView {
    private val permissionAlertDialogNeutralClickRelay = PublishRelay.create<Any>()
    override fun getPermissionAlertDialogNeutralClickObservable(): Observable<Any> {
        return permissionAlertDialogNeutralClickRelay
    }

    override fun showPermissionAlertDialog() {
        val builder = AlertDialog.Builder(context!!)
        builder.setMessage(R.string.permission_dialog_message)
            .setTitle(R.string.permission_dialog_title)
        builder.setNeutralButton(R.string.ok) { _, _ ->
            permissionAlertDialogNeutralClickRelay.accept(Any())
        }
        val dialog = builder.create()
        dialog.show()
    }

    override fun exitApp() {
        activity?.finish()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        // Create the view
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val database = DestinationDatabase.getInstance(context!!)
        Thread {

            //            database.destinationDao().insert(DestinationEntity(0.0, 0.0, "for_fun"))
//            database.destinationDao().insert(DestinationEntity(0.0, 0.0, "for_fun"))
//            var entries = database.destinationDao().getAll()
//            database.destinationDao().delete(entries[0])
//            database.destinationDao().delete(entries[1])
            val entries = DestinationLoader(LocalDestinationRepository(database)).execute(Any()).blockingGet()
            println(entries)
        }.start()

    }

    override fun requestReceiveSmsPermission(requestCode: Int) {
        requestPermission(Manifest.permission.RECEIVE_SMS, requestCode)
    }

    override fun requestReadSmsPermission(requestCode: Int) {
        requestPermission(Manifest.permission.READ_SMS, requestCode)
    }

    private fun requestPermission(permission: String, requestCode: Int) {
        requestPermissions(arrayOf(permission), requestCode)
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


    private val permissionResultRelay = PublishRelay.create<PermissionResult>()
    override fun getPermissionResultObservable(): Observable<PermissionResult> {
        return permissionResultRelay
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionResultRelay.accept(PermissionResult(requestCode, grantResults))
    }
}
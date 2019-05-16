package de.infoware.smsparser.ui

import android.content.pm.PackageManager
import de.infoware.smsparser.permission.PermissionResult
import net.grandcentrix.thirtyinch.TiPresenter
import net.grandcentrix.thirtyinch.rx2.RxTiPresenterDisposableHandler
import io.reactivex.Observable

class MainPresenter : TiPresenter<MainView>() {

    /**
     * Helper class, which disposes [io.reactivex.disposables.Disposable] from the view
     * or other [Observable]s.
     */
    private val handler = RxTiPresenterDisposableHandler(this)

    private val receiveSmsRequestCode = 100
    private val readSmsRequestCode = 101

    var receiveSmsAllowed = false
    var readSmsAllowed = false

    override fun onAttachView(view: MainView) {
        super.onAttachView(view)

        checkPermissions(view)

        if (receiveSmsAllowed && readSmsAllowed) {
            view.navigateToMainFragment()
        }
        if (!receiveSmsAllowed) {
            view.requestReceiveSmsPermission(receiveSmsRequestCode)
        }

        if (!readSmsAllowed) {
            view.requestReadSmsPermission(readSmsRequestCode)
        }

        subscribeToUiEvents(view)
    }

    private fun subscribeToUiEvents(view: MainView) {
        handler.manageViewDisposable(view.getPermissionResultObservable()
            .subscribe {
                processPermissionResult(it)
            }
        )
    }

    private fun processPermissionResult(permissionResult: PermissionResult) {
        val requestCode = permissionResult.requestCode
        val grantResults = permissionResult.grantResults

        // Check if any permission is granted in general and when granted - check which one.
        if (isPermissionGranted(grantResults)) {
            when (requestCode) {
                receiveSmsRequestCode -> {
                    receiveSmsAllowed = true
                }
                readSmsRequestCode -> {
                    readSmsAllowed = true
                }
            }
        }
    }

    private fun checkPermissions(view: MainView) {
        receiveSmsAllowed = view.checkReceiveSmsPermission()
        readSmsAllowed = view.checkReadSmsPermission()
    }

    private fun isPermissionGranted(grantResults: IntArray): Boolean {
        return grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
    }


}
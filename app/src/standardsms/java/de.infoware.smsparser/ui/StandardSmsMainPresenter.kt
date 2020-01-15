package de.infoware.smsparser.ui

import de.infoware.smsparser.permission.PermissionResult
import de.infoware.smsparser.ui.presenter.MainPresenter
import de.infoware.smsparser.ui.view.MainView

class StandardSmsMainPresenter : MainPresenter() {

    private val receiveSmsRequestCode = 100
    private val readSmsRequestCode = 101

    private var receiveSmsAllowed = false
    private var readSmsAllowed = false

    override fun processPermissionResult(view: MainView, permissionResult: PermissionResult) {
        val requestCode = permissionResult.requestCode
        val grantResults = permissionResult.grantResults

        // Check if any permission is granted in general and when granted - check which one.
        if (isPermissionGranted(grantResults)) {
            when (requestCode) {
                receiveSmsRequestCode -> receiveSmsAllowed = true

                readSmsRequestCode -> readSmsAllowed = true
            }
        } else {
            view.showPermissionAlertDialog()
        }
    }

    override fun checkPermissions(view: MainView) {
        if (view !is StandardSmsMainView) {
            return
        }
        receiveSmsAllowed = view.checkReceiveSmsPermission()
        readSmsAllowed = view.checkReadSmsPermission()
    }

    override fun requestPermissionsIfRequired(view: MainView) {
        if (view !is StandardSmsMainView) {
            return
        }

        if (!receiveSmsAllowed) {
            view.requestReceiveSmsPermission(receiveSmsRequestCode)
        }
        if (!readSmsAllowed) {
            view.requestReadSmsPermission(readSmsRequestCode)
        }
    }
}
package de.infoware.smsparser.ui

import de.infoware.smsparser.permission.PermissionResult
import de.infoware.smsparser.ui.presenter.MainPresenter
import de.infoware.smsparser.ui.view.MainView

class TetraMainPresenter : MainPresenter() {
    private val serialPortPermissionRequestCode = 102

    private var serialPortAllowed = false

    override fun onAttachView(view: MainView) {
        super.onAttachView(view)

        if (view !is TetraMainView) {
            return
        }

        if (serialPortAllowed) {
            view.startTetraConnectionService()
        }

        subscribeToPortError(view)
    }

    private fun subscribeToPortError(view: MainView) {
        if (view !is TetraMainView) {
            return
        }

        handler.manageViewDisposable(view.getPortFailureObservable()
            .subscribe { view.showPortFailureAlertDialog(it) }
        )

        handler.manageViewDisposable(view.getOnPortFailureDialogNeutralClickedOnservable()
            .subscribe { view.exitApp() }
        )
    }

    override fun processPermissionResult(view: MainView, permissionResult: PermissionResult) {
        if (view !is TetraMainView) {
            return
        }

        val requestCode = permissionResult.requestCode
        val grantResults = permissionResult.grantResults

        // Check if any permission is granted in general and when granted - check which one.
        if (isPermissionGranted(grantResults)) {
            when (requestCode) {
                serialPortPermissionRequestCode -> serialPortAllowed = true
            }
            view.startTetraConnectionService()
        } else {
            view.showPermissionAlertDialog()
        }
    }

    override fun checkPermissions(view: MainView) {
        if (view !is TetraMainView) {
            return
        }
        serialPortAllowed = view.checkSerialPortPermission()
    }

    override fun requestPermissionsIfRequired(view: MainView) {
        if (view !is TetraMainView) {
            return
        }
        if (!serialPortAllowed) {
            view.requestSerialPortPermission(serialPortPermissionRequestCode)
        }
    }
}
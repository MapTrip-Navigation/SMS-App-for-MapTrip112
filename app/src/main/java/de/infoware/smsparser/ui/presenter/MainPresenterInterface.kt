package de.infoware.smsparser.ui.presenter

import de.infoware.smsparser.permission.PermissionResult
import de.infoware.smsparser.ui.view.MainView

interface MainPresenterInterface {
    fun processPermissionResult(view: MainView, permissionResult: PermissionResult)

    fun checkPermissions(view: MainView)

    fun requestPermissionsIfRequired(view: MainView)
}
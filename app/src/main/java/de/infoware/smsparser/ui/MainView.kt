package de.infoware.smsparser.ui

import de.infoware.smsparser.permission.PermissionResult
import io.reactivex.Observable
import net.grandcentrix.thirtyinch.TiView

interface MainView : TiView {

    fun checkReceiveSmsPermission(): Boolean

    fun requestReceiveSmsPermission(requestCode: Int)

    fun checkReadSmsPermission(): Boolean

    fun requestReadSmsPermission(requestCode: Int)

    fun getPermissionResultObservable(): Observable<PermissionResult>

    fun getPermissionAlertDialogNeutralClickObservable(): Observable<Any>

    fun showPermissionAlertDialog()

    fun exitApp()
}
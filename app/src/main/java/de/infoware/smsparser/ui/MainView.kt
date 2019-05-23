package de.infoware.smsparser.ui

import de.infoware.smsparser.DestinationInfo
import de.infoware.smsparser.permission.PermissionResult
import de.infoware.smsparser.storage.DataSource
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

    fun getDataSource(): DataSource

    fun updateDestinationInfoList(newDestinationInfoList: List<DestinationInfo>)

    fun getOnDestinationInfoClickObservable(): Observable<DestinationInfo>

    fun getOnStartNavigationClickObservable(): Observable<DestinationInfo>

    fun showNavigationDialog(destinationInfo: DestinationInfo)

    fun startMapTripWithDestinationInfo(destinationInfo: DestinationInfo)

    fun getOnDeleteMenuClickObservable(): Observable<Any>

    fun getOnDeleteApprovedClickObservable(): Observable<Any>

    fun showDeleteListDialog()

    fun exitApp()
}
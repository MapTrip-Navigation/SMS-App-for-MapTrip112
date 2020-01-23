package de.infoware.smsparser.ui.view

import de.infoware.smsparser.data.DestinationInfo
import de.infoware.smsparser.permission.PermissionResult
import de.infoware.smsparser.data.DataSource
import io.reactivex.Observable
import net.grandcentrix.thirtyinch.TiView

interface MainView : TiView {

    fun getPermissionResultObservable(): Observable<PermissionResult>

    // Observable for clicks on the neutral button of the dialog about not-granted permissions.
    fun getPermissionAlertDialogNeutralClickObservable(): Observable<Any>

    fun showPermissionAlertDialog()

    // Provides data source. Can be any data storage. Each view decides itself.
    fun getDataSource(): DataSource

    fun updateDestinationInfoList(newDestinationInfoList: List<DestinationInfo>)

    fun getOnDestinationInfoClickObservable(): Observable<DestinationInfo>

    fun getOnStartNavigationClickObservable(): Observable<DestinationInfo>

    fun showNavigationDialog(destinationInfo: DestinationInfo)

    fun startMapTripWithDestinationInfo(destinationInfo: DestinationInfo)

    fun showToastMapTripNotFound ()

    fun getOnDeleteMenuClickObservable(): Observable<Any>

    fun getOnDeleteApprovedClickObservable(): Observable<Any>

    fun showDeleteListDialog()

    fun exitApp()
}
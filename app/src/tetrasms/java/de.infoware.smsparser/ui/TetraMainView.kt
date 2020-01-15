package de.infoware.smsparser.ui

import de.infoware.smsparser.ui.view.MainView
import io.reactivex.Observable

interface TetraMainView : MainView {

    fun checkSerialPortPermission(): Boolean

    fun requestSerialPortPermission(requestCode: Int)

    fun startTetraConnectionService()

    fun getPortFailureObservable(): Observable<String>

    fun showPortFailureAlertDialog(errorMessage: String)

    fun getOnPortFailureDialogNeutralClickedOnservable(): Observable<Any>
}
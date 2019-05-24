package de.infoware.smsparser.ui

import android.content.pm.PackageManager
import de.infoware.smsparser.data.DestinationInfo
import de.infoware.smsparser.data.storage.DestinationDatabase
import de.infoware.smsparser.domain.DestinationEraser
import de.infoware.smsparser.domain.DestinationLoader
import de.infoware.smsparser.domain.DestinationUpdater
import de.infoware.smsparser.permission.PermissionResult
import de.infoware.smsparser.repository.LocalDestinationRepository
import de.infoware.smsparser.ui.util.clickDebounceInMillis
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import net.grandcentrix.thirtyinch.TiPresenter
import net.grandcentrix.thirtyinch.rx2.RxTiPresenterDisposableHandler
import java.util.concurrent.TimeUnit

class MainPresenter : TiPresenter<MainView>() {

    /**
     * Helper class, which disposes [io.reactivex.disposables.Disposable] from the view
     * or other [Observable]s.
     */
    private val handler = RxTiPresenterDisposableHandler(this)

    private val receiveSmsRequestCode = 100
    private val readSmsRequestCode = 101

    private var receiveSmsAllowed = false
    private var readSmsAllowed = false

    private val defaultNumberOfAffectedItems = 0

    override fun onAttachView(view: MainView) {
        super.onAttachView(view)

        checkPermissions(view)

        if (!receiveSmsAllowed) {
            view.requestReceiveSmsPermission(receiveSmsRequestCode)
        }

        if (!readSmsAllowed) {
            view.requestReadSmsPermission(readSmsRequestCode)
        }

        // If the latest destination is not yet navigated, show navigation dialog directly
        handler.manageViewDisposable(loadDestinationInfo(view)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { destinationInfoList ->
                view.updateDestinationInfoList(destinationInfoList)
            }
            .flatMapMaybe { destinationInfoList ->
                if (destinationInfoList.isNotEmpty() && !destinationInfoList[0].alreadyNavigated) {
                    view.showNavigationDialog(destinationInfoList[0])
                    return@flatMapMaybe updateNavigatedStatus(view, destinationInfoList[0], true)
                }
                return@flatMapMaybe Maybe.fromCallable { return@fromCallable defaultNumberOfAffectedItems }
            }
            .subscribe())

        subscribeToUiEvents(view)
    }

    private fun subscribeToUiEvents(view: MainView) {
        handler.manageViewDisposable(view.getPermissionResultObservable()
            .subscribe { processPermissionResult(view, it) }
        )

        // Handles clicks of the neutral button of the dialog with non-granted permission
        handler.manageViewDisposable(view.getPermissionAlertDialogNeutralClickObservable()
            .subscribe { view.exitApp() }
        )

        handler.manageViewDisposable(view.getOnDestinationInfoClickObservable()
            .debounce(clickDebounceInMillis, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { view.showNavigationDialog(it) }
        )

        handler.manageViewDisposable(view.getOnStartNavigationClickObservable()
            .debounce(clickDebounceInMillis, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { view.startMapTripWithDestinationInfo(it) }
        )

        handler.manageViewDisposable(view.getOnDeleteMenuClickObservable()
            .debounce(clickDebounceInMillis, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { view.showDeleteListDialog() }
        )

        handler.manageViewDisposable(view.getOnDeleteApprovedClickObservable()
            .debounce(clickDebounceInMillis, TimeUnit.MILLISECONDS)
            .flatMapMaybe { deleteAllEntriesFromDataStore(view) }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { view.updateDestinationInfoList(ArrayList()) }
            .subscribe()
        )
    }

    private fun processPermissionResult(view: MainView, permissionResult: PermissionResult) {
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

    private fun checkPermissions(view: MainView) {
        receiveSmsAllowed = view.checkReceiveSmsPermission()
        readSmsAllowed = view.checkReadSmsPermission()
    }

    private fun isPermissionGranted(grantResults: IntArray): Boolean {
        return grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
    }

    private fun loadDestinationInfo(view: MainView): Single<List<DestinationInfo>> {
        val dataSource = view.getDataSource()
        if (dataSource is DestinationDatabase) {
            return DestinationLoader(LocalDestinationRepository(dataSource)).execute(Any())
        }
        return Single.fromCallable { ArrayList<DestinationInfo>() }
    }

    private fun updateNavigatedStatus(
        view: MainView, destinationInfo: DestinationInfo,
        navigatedStatus: Boolean
    ): Maybe<Int> {
        destinationInfo.alreadyNavigated = navigatedStatus
        val dataSource = view.getDataSource()
        if (dataSource is DestinationDatabase) {
            return DestinationUpdater(LocalDestinationRepository(dataSource)).execute(destinationInfo)
        }
        return Maybe.fromCallable { return@fromCallable defaultNumberOfAffectedItems }
    }

    private fun deleteAllEntriesFromDataStore(view: MainView): Maybe<Int> {
        val dataSource = view.getDataSource()
        if (dataSource is DestinationDatabase) {
            return DestinationEraser(LocalDestinationRepository(dataSource)).execute(Any())
        }
        return Maybe.fromCallable { return@fromCallable defaultNumberOfAffectedItems }
    }
}
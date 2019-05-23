package de.infoware.smsparser.ui

import android.content.pm.PackageManager
import de.infoware.smsparser.DestinationInfo
import de.infoware.smsparser.domain.DestinationEraser
import de.infoware.smsparser.domain.DestinationLoader
import de.infoware.smsparser.domain.DestinationUpdater
import de.infoware.smsparser.permission.PermissionResult
import de.infoware.smsparser.repository.LocalDestinationRepository
import de.infoware.smsparser.storage.DestinationDatabase
import de.infoware.smsparser.ui.util.clickDebounceInMillis
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
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

    override fun onAttachView(view: MainView) {
        super.onAttachView(view)

        checkPermissions(view)

        if (!receiveSmsAllowed) {
            view.requestReceiveSmsPermission(receiveSmsRequestCode)
        }

        if (!readSmsAllowed) {
            view.requestReadSmsPermission(readSmsRequestCode)
        }

        handler.manageViewDisposable(loadDestinationInfo(view)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { destinationInfoList ->
                view.updateDestinationInfoList(destinationInfoList)
                if (destinationInfoList.isNotEmpty() && !destinationInfoList[0].alreadyNavigated) {
                    view.showNavigationDialog(destinationInfoList[0])
                }
            })

        subscribeToUiEvents(view)
    }

    private fun subscribeToUiEvents(view: MainView) {
        handler.manageViewDisposable(view.getPermissionResultObservable()
            .subscribe { processPermissionResult(view, it) }
        )

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
            .doOnNext { view.startMapTripWithDestinationInfo(it) }
            .flatMapCompletable { updateNavigatedStatus(view, it, true) }
            .subscribe()
        )

        handler.manageViewDisposable(view.getOnDeleteApprovedClickObservable()
            .debounce(clickDebounceInMillis, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { view.updateDestinationInfoList(ArrayList()) }
            .observeOn(Schedulers.io())
            .flatMapCompletable { deleteAllEntriesFromDataStore(view) }
            .subscribe()
        )

        handler.manageViewDisposable(view.getOnDeleteMenuClickObservable()
            .debounce(clickDebounceInMillis, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                view.showDeleteListDialog()
            }
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
    ): Completable {
        destinationInfo.alreadyNavigated = navigatedStatus
        val dataSource = view.getDataSource()
        if (dataSource is DestinationDatabase) {
            return DestinationUpdater(LocalDestinationRepository(dataSource)).execute(destinationInfo)
        }
        return Completable.complete()
    }

    private fun deleteAllEntriesFromDataStore(view: MainView): Completable {
        val dataSource = view.getDataSource()
        if (dataSource is DestinationDatabase) {
            return DestinationEraser(LocalDestinationRepository(dataSource)).execute(Any())
        }
        return Completable.complete()
    }
}
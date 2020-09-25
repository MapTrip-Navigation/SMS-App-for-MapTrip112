package de.infoware.smsparser.ui.presenter

import android.content.pm.PackageManager
import de.infoware.android.mti.enums.ApiError
import de.infoware.smsparser.data.DestinationInfo
import de.infoware.smsparser.data.storage.DestinationDatabase
import de.infoware.smsparser.domain.DestinationEraser
import de.infoware.smsparser.domain.DestinationLoader
import de.infoware.smsparser.domain.DestinationUpdater
import de.infoware.smsparser.repository.LocalDestinationRepository
import de.infoware.smsparser.ui.util.clickDebounceInMillis
import de.infoware.smsparser.ui.view.MainView
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import net.grandcentrix.thirtyinch.TiPresenter
import net.grandcentrix.thirtyinch.rx2.RxTiPresenterDisposableHandler
import java.util.concurrent.TimeUnit

abstract class MainPresenter : TiPresenter<MainView>(),
    MainPresenterInterface {

    /**
     * Helper class, which disposes [io.reactivex.disposables.Disposable] from the view
     * or other [Observable]s.
     */
    val handler = RxTiPresenterDisposableHandler(this)

    private val defaultNumberOfAffectedItems = 0

    private val checkMapTripStarted = false

    override fun onAttachView(view: MainView) {
        super.onAttachView(view)

        checkPermissions(view)

        requestPermissionsIfRequired(view)

        // If the latest destination is not yet navigated, show navigation dialog directly
        handler.manageViewDisposable(loadDestinationInfo(view)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { destinationInfoList ->
                view.updateDestinationInfoList(destinationInfoList)
            }
            .flatMapMaybe { destinationInfoList ->
                if (destinationInfoList.isNotEmpty() && !destinationInfoList[0].alreadyNavigated) {
                    view.showNavigationDialog(destinationInfoList[0])
                    return@flatMapMaybe updateNavigatedStatus(
                        view,
                        destinationInfoList[0],
                        true
                    )
                }
                return@flatMapMaybe Maybe.just(defaultNumberOfAffectedItems)
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

        handler.manageViewDisposable(
            view.getOnStartNavigationClickObservable()
                .debounce(clickDebounceInMillis, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { view.startMapTripWithDestinationInfo(it) },
                    { view.showToastMapTripNotFound() })
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

        handler.manageViewDisposable(view.getOnInitResultObservable()
            .filter { it == ApiError.TIMEOUT && checkMapTripStarted }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { view.showMapTripIsNotStartedDialog() }
        )

        handler.manageViewDisposable(
            view.getOnStartMapTripClickObservable()
                .debounce(clickDebounceInMillis, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { view.startMapTrip() },
                    { exception -> view.showToastMapTripNotFound() }
                )
        )
    }

    open fun isPermissionGranted(grantResults: IntArray): Boolean {
        return grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
    }

    private fun loadDestinationInfo(view: MainView): Single<List<DestinationInfo>> {
        val dataSource = view.getDataSource()
        if (dataSource is DestinationDatabase) {
            return DestinationLoader(LocalDestinationRepository(dataSource)).execute(Any())
        }
        return Single.just(ArrayList())
    }

    private fun updateNavigatedStatus(
        view: MainView, destinationInfo: DestinationInfo,
        navigatedStatus: Boolean
    ): Maybe<Int> {
        destinationInfo.alreadyNavigated = navigatedStatus
        val dataSource = view.getDataSource()
        if (dataSource is DestinationDatabase) {
            return DestinationUpdater(LocalDestinationRepository(dataSource)).execute(
                destinationInfo
            )
        }
        return Maybe.just(defaultNumberOfAffectedItems)
    }

    private fun deleteAllEntriesFromDataStore(view: MainView): Maybe<Int> {
        val dataSource = view.getDataSource()
        if (dataSource is DestinationDatabase) {
            return DestinationEraser(LocalDestinationRepository(dataSource)).execute(Any())
        }
        return Maybe.just(defaultNumberOfAffectedItems)
    }
}
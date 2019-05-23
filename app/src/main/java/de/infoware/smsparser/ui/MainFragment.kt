package de.infoware.smsparser.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxrelay2.PublishRelay
import de.infoware.smsparser.DestinationInfo
import de.infoware.smsparser.R
import de.infoware.smsparser.permission.PermissionResult
import de.infoware.smsparser.storage.DataSource
import de.infoware.smsparser.storage.DestinationDatabase
import de.infoware.smsparser.ui.adapter.DestinationInfoAdapter
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_main.*
import net.grandcentrix.thirtyinch.TiFragment


class MainFragment : TiFragment<MainPresenter, MainView>(), MainView {

    override fun startMapTripWithDestinationInfo(destinationInfo: DestinationInfo) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(
                "maptrip://navigate?latitude=${destinationInfo.lat}" +
                        "&longitude=${destinationInfo.lon}"
            )
        )
        context?.startActivity(intent)
    }


    private var onStartNavigationClickPublishRelay = PublishRelay.create<DestinationInfo>()

    override fun getOnStartNavigationClickObservable(): Observable<DestinationInfo> {
        return onStartNavigationClickPublishRelay
    }

    override fun showNavigationDialog(destinationInfo: DestinationInfo) {
        val builder = AlertDialog.Builder(activity!!)

        builder.setMessage(destinationInfo.reason)
            .setTitle(R.string.navigation_start_dialog_title)

        builder.setNegativeButton(R.string.cancel) { _, _ -> }

        builder.setPositiveButton(R.string.start_navigation) { _, _ ->
            onStartNavigationClickPublishRelay.accept(destinationInfo)
        }
        val dialog = builder.create()
        dialog.show()
    }

    private var onDestinationInfoClickPublishRelay = PublishRelay.create<DestinationInfo>()

    override fun getOnDestinationInfoClickObservable(): Observable<DestinationInfo> {
        return onDestinationInfoClickPublishRelay
    }

    private val permissionAlertDialogNeutralClickRelay = PublishRelay.create<Any>()
    override fun getPermissionAlertDialogNeutralClickObservable(): Observable<Any> {
        return permissionAlertDialogNeutralClickRelay
    }

    override fun showPermissionAlertDialog() {
        val builder = AlertDialog.Builder(context!!)
        builder.setMessage(R.string.permission_dialog_message)
            .setTitle(R.string.permission_dialog_title)
        builder.setNeutralButton(R.string.ok) { _, _ ->
            permissionAlertDialogNeutralClickRelay.accept(Any())
        }
        val dialog = builder.create()
        dialog.show()
    }

    override fun exitApp() {
        activity?.finish()
    }

    override fun getDataSource(): DataSource {
        return DestinationDatabase.getInstance(context!!)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        // Create the view
        return inflater.inflate(R.layout.fragment_main, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeAdapter()
        initializeRecyclerView()
    }

    private lateinit var adapter: DestinationInfoAdapter
    private fun initializeAdapter() {
        adapter = DestinationInfoAdapter()
        adapter.setOnDestinationInfoClickListener(onDestinationInfoClickPublishRelay::accept)
    }


    private fun initializeRecyclerView() {
        val layoutManager = LinearLayoutManager(context)

        rvDestinationInfo.layoutManager = layoutManager
        rvDestinationInfo.adapter = adapter

        val dividerItemDecoration = DividerItemDecoration(
            context,
            layoutManager.orientation
        )
        rvDestinationInfo.addItemDecoration(dividerItemDecoration)
    }

    override fun updateDestinationInfoList(newDestinationInfoList: List<DestinationInfo>) {
        adapter.replaceEntries(newDestinationInfoList)
    }

    override fun requestReceiveSmsPermission(requestCode: Int) {
        requestPermission(Manifest.permission.RECEIVE_SMS, requestCode)
    }

    override fun requestReadSmsPermission(requestCode: Int) {
        requestPermission(Manifest.permission.READ_SMS, requestCode)
    }

    private fun requestPermission(permission: String, requestCode: Int) {
        requestPermissions(arrayOf(permission), requestCode)
    }

    override fun checkReceiveSmsPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            activity!! as MainActivity, Manifest.permission.RECEIVE_SMS
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun checkReadSmsPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            activity!! as MainActivity, Manifest.permission.READ_SMS
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun providePresenter(): MainPresenter {
        return MainPresenter()
    }


    private val permissionResultRelay = PublishRelay.create<PermissionResult>()
    override fun getPermissionResultObservable(): Observable<PermissionResult> {
        return permissionResultRelay
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionResultRelay.accept(PermissionResult(requestCode, grantResults))
    }
}
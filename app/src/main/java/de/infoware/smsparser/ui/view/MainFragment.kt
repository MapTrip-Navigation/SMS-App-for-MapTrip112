package de.infoware.smsparser.ui.view

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxrelay2.PublishRelay
import de.infoware.smsparser.data.DestinationInfo
import de.infoware.smsparser.R
import de.infoware.smsparser.permission.PermissionResult
import de.infoware.smsparser.data.DataSource
import de.infoware.smsparser.data.storage.DestinationDatabase
import de.infoware.smsparser.ui.adapter.DestinationInfoAdapter
import de.infoware.smsparser.ui.presenter.MainPresenter
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_main.*
import net.grandcentrix.thirtyinch.TiFragment


abstract class MainFragment : TiFragment<MainPresenter, MainView>(),
    MainView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    // Handles click on the neutral button of the dialog about not-granted permissions.
    private val permissionAlertDialogNeutralClickRelay = PublishRelay.create<Any>()

    override fun getPermissionAlertDialogNeutralClickObservable(): Observable<Any> {
        return permissionAlertDialogNeutralClickRelay
    }

    // This dialog is shown when user does not grant the requested permissions.
    override fun showPermissionAlertDialog() {
        val builder = AlertDialog.Builder(context!!)
        builder.setMessage(R.string.permission_dialog_message)
            .setTitle(R.string.attention)
        builder.setNeutralButton(R.string.ok) { _, _ ->
            permissionAlertDialogNeutralClickRelay.accept(Any())
        }
        val dialog = builder.create()
        dialog.show()
    }

    override fun exitApp() {
        activity?.finish()
    }

    fun requestPermission(permission: String, requestCode: Int) {
        requestPermissions(arrayOf(permission), requestCode)
    }

    private val permissionResultRelay = PublishRelay.create<PermissionResult>()
    override fun getPermissionResultObservable(): Observable<PermissionResult> {
        return permissionResultRelay
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionResultRelay.accept(PermissionResult(requestCode, grantResults))
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        // Create the view
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    // Handles clicks on the destination list items.
    private var onDestinationInfoClickPublishRelay = PublishRelay.create<DestinationInfo>()

    override fun getOnDestinationInfoClickObservable(): Observable<DestinationInfo> {
        return onDestinationInfoClickPublishRelay
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


    // Handles clicks on the delete menu item
    private var onDeleteMenuClickPublishRelay = PublishRelay.create<Any>()

    override fun getOnDeleteMenuClickObservable(): Observable<Any> {
        return onDeleteMenuClickPublishRelay
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        if (item.itemId == R.id.menu_main_delete) {
            onDeleteMenuClickPublishRelay.accept(Any())
        }

        return true
    }

    // Shows delete dialog and request for the approval
    override fun showDeleteListDialog() {
        val builder = AlertDialog.Builder(activity!!)

        builder.setMessage(R.string.delete_list)
            .setTitle(R.string.attention)

        builder.setNegativeButton(R.string.cancel) { _, _ -> }

        builder.setPositiveButton(R.string.ok) { _, _ ->
            onDeleteApprovedClickPublishRelay.accept(Any())
        }
        val dialog = builder.create()
        dialog.show()
    }

    // Handles clicks on the approval button inside delete dialog.
    private var onDeleteApprovedClickPublishRelay = PublishRelay.create<Any>()

    override fun getOnDeleteApprovedClickObservable(): Observable<Any> {
        return onDeleteApprovedClickPublishRelay
    }

    // Handles clicks on the startSession navigation button inside the dialog
    private var onStartNavigationClickPublishRelay = PublishRelay.create<DestinationInfo>()

    override fun getOnStartNavigationClickObservable(): Observable<DestinationInfo> {
        return onStartNavigationClickPublishRelay
    }

    // Shows startSession navigation dialog.
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

    // Starts maptrip with navigation to the provided destination.
    override fun startMapTripWithDestinationInfo(destinationInfo: DestinationInfo) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(
                "maptrip://navigate?latitude=${destinationInfo.lat}" +
                        "&longitude=${destinationInfo.lon}"
            )
        )
        try {
            context?.startActivity(intent)
        } catch (exc: ActivityNotFoundException) {
            Toast.makeText(context, R.string.maptrip_start_fail, Toast.LENGTH_LONG).show()
        }
    }

    override fun getDataSource(): DataSource {
        return DestinationDatabase.getInstance(context!!)
    }

}
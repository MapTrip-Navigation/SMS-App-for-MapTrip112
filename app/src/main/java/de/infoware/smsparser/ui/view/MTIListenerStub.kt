package de.infoware.smsparser.ui.view

import de.infoware.android.mti.ApiListener
import de.infoware.android.mti.NavigationListener
import de.infoware.android.mti.enums.ApiError
import de.infoware.android.mti.enums.Info

interface MTIListenerStub : ApiListener, NavigationListener {

    override fun stopServerResult(p0: Int, p1: ApiError?) {

    }

    override fun hideServerResult(p0: Int, p1: ApiError?) {

    }

    override fun customFunctionResult(p0: Int, p1: String?, p2: String?, p3: ApiError?) {

    }

    override fun sendTextResult(p0: Int, p1: ApiError?) {

    }

    override fun setDataUsageMonthlyLimitResult(p0: Int, p1: ApiError?) {

    }

    override fun resetDataUsageMonthlyLimitResult(p0: Int, p1: ApiError?) {

    }

    override fun showAppResult(p0: Int, p1: ApiError?) {

    }

    override fun getApiVersionResult(p0: Int, p1: String?, p2: ApiError?) {

    }

    override fun infoMsg(p0: Info?, p1: Int) {

    }

    override fun getMapVersionResult(p0: Int, p1: String?, p2: ApiError?) {

    }

    override fun getDataUsageMonthlyLimitResult(p0: Int, p1: Int, p2: ApiError?) {

    }

    override fun showServerResult(p0: Int, p1: ApiError?) {

    }

    override fun onError(p0: Int, p1: String?, p2: ApiError?) {

    }

    override fun enableNetworkConnectionsResult(p0: Int, p1: ApiError?) {

    }

    override fun getDataUsageRemainingQuotaResult(p0: Int, p1: Int, p2: ApiError?) {

    }

    override fun findServerResult(p0: Int, p1: ApiError?) {

    }

    override fun isNetworkConnectionEnabledResult(p0: Int, p1: Boolean, p2: ApiError?) {

    }

    override fun getMaptripVersionResult(p0: Int, p1: String?, p2: ApiError?) {

    }

    override fun statusInfo(p0: Double, p1: Double, p2: Double, p3: Double) {
        
    }

    override fun coiInfo(p0: Double, p1: Double, p2: String?, p3: String?, p4: Double) {
        
    }

    override fun crossingInfo(
        p0: Double,
        p1: Double,
        p2: String?,
        p3: String?,
        p4: String?,
        p5: Double
    ) {
        
    }

    override fun destinationReached(p0: Int) {
        
    }

    override fun insertDestinationCoordinateResult(p0: Int, p1: ApiError?) {
        
    }

    override fun appendDestinationCoordinateResult(p0: Int, p1: Int, p2: ApiError?) {
        
    }

    override fun insertDestinationAddressResult(p0: Int, p1: ApiError?) {
        
    }

    override fun appendDestinationAddressResult(p0: Int, p1: Int, p2: ApiError?) {
        
    }

    override fun insertGeocodedDestinationResult(p0: Int, p1: ApiError?) {
        
    }

    override fun appendGeocodedDestinationResult(p0: Int, p1: Int, p2: ApiError?) {
        
    }

    override fun markDestinationCoordinateAsViaPointResult(p0: Int, p1: ApiError?) {
        
    }

    override fun getDestinationCoordinateResult(p0: Int, p1: ApiError?, p2: Double, p3: Double) {
        
    }

    override fun getDestinationCoordinateCountResult(p0: Int, p1: ApiError?, p2: Int) {
        
    }

    override fun getCurrentDestinationResult(p0: Int, p1: ApiError?, p2: Int) {
        
    }

    override fun removeAllDestinationCoordinatesResult(p0: Int, p1: ApiError?) {
        
    }

    override fun startNavigationResult(p0: Int, p1: ApiError?) {
        
    }

    override fun startAlternativeNavigationResult(p0: Int, p1: ApiError?) {
        
    }

    override fun startSimulationResult(p0: Int, p1: ApiError?) {
        
    }

    override fun stopNavigationResult(p0: Int, p1: ApiError?) {
        
    }

    override fun startRouteFromFileResult(p0: Int, p1: ApiError?) {
        
    }

    override fun startReferenceRouteResult(p0: Int, p1: ApiError?) {
        
    }

    override fun getReferenceRouteFileResult(p0: Int, p1: String?, p2: ApiError?) {
        
    }

    override fun syncWithActiveNavigationResult(p0: Int, p1: ApiError?) {
        
    }

    override fun navigateWithGuiGeocodingResult(p0: Int, p1: ApiError?) {
        
    }

    override fun routingStarted() {
        
    }

    override fun routeCalculated() {
        
    }

    override fun setRoutingModeHybridResult(p0: Int, p1: ApiError?) {
        
    }

    override fun newRouteAvailable() {
        
    }

    override fun setEmergencyRoutingEnabledResult(p0: Int, p1: ApiError?) {
        
    }

    override fun getEmergencyRoutingEnabledResult(
        p0: Int,
        p1: ApiError?,
        p2: Boolean,
        p3: Boolean,
        p4: Int
    ) {
        
    }

    override fun setEmergencyRouteRadiusResult(p0: Int, p1: ApiError?) {
        
    }

    override fun getEmergencyRouteRadiusResult(p0: Int, p1: ApiError?, p2: Int) {
        
    }

    override fun calculateRoutesResult(p0: Int, p1: ApiError?) {

    }

    override fun startTourResult(p0: Int, p1: ApiError?) {

    }

    override fun skipNextDestinationResult(p0: Int, p1: ApiError?) {

    }

    override fun setAsReferenceRouteResult(p0: Int, p1: ApiError?) {

    }
}
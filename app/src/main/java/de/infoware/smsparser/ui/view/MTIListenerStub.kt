package de.infoware.smsparser.ui.view

import de.infoware.android.mti.ApiListener
import de.infoware.android.mti.enums.ApiError
import de.infoware.android.mti.enums.Info

interface MTIListenerStub : ApiListener {
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
}
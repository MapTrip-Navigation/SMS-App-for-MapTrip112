package de.infoware.smsparser.ui

import de.infoware.smsparser.ui.view.MainView

interface StandardSmsMainView : MainView {

    fun checkReceiveSmsPermission(): Boolean

    fun requestReceiveSmsPermission(requestCode: Int)

    fun checkReadSmsPermission(): Boolean

    fun requestReadSmsPermission(requestCode: Int)

}
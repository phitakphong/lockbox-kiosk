package com.lockboxth.lockboxkiosk.http.model.kiosk

data class KioskMonitorRequest(
    val generalprofile_id: Int ,
    val run_background: Boolean
)
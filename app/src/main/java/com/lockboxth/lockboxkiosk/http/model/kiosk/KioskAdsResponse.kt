package com.lockboxth.lockboxkiosk.http.model.kiosk

data class KioskAdsResponse(
    val screen_saver: ArrayList<ScreenServer>
)

data class ScreenServer(
    val level: Int,
    val source: String,
    val type: String,
    val time: Int
)

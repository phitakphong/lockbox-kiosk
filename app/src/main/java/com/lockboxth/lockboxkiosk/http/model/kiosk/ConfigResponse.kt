package com.lockboxth.lockboxkiosk.http.model.kiosk

data class ConfigResponse(
    val main_menu: List<MainMenuItem>,
)

data class MainMenuItem(
    val id: Int,
    val service: String
)

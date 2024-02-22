package com.lockboxth.lockboxkiosk.http.model.kiosk


data class RegisterKiosk(
    val serial_number: String,
    val master_key: String,
    val fcm_token: String
)
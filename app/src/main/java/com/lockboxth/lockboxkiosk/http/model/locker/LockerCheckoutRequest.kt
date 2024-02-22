package com.lockboxth.lockboxkiosk.http.model.locker

data class LockerCheckoutRequest(
    val generalprofile_id: Int,
    val txn: String,
    val screenshot: String
)

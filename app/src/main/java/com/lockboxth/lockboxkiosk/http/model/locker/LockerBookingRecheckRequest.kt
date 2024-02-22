package com.lockboxth.lockboxkiosk.http.model.locker

data class LockerBookingRecheckRequest(
    val generalprofile_id: Int,
    val txn: String,
    val booking_id: Int,
    val phone: String
)


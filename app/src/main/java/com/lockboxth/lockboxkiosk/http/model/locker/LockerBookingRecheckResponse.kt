package com.lockboxth.lockboxkiosk.http.model.locker

data class LockerBookingRecheckResponse(
    val phone: String,
    val verify_type: String,
    val has_pdpa: Boolean
)

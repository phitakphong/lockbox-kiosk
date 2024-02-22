package com.lockboxth.lockboxkiosk.http.model.personal

data class VerifyBookingResponse(
    val txn: String,
    val booking_id: Int
)


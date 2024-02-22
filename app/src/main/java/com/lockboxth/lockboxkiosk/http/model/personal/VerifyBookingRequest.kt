package com.lockboxth.lockboxkiosk.http.model.personal

data class VerifyBookingRequest(
    val generalprofile_id: Int,
    val type: String,
    val data: String
)


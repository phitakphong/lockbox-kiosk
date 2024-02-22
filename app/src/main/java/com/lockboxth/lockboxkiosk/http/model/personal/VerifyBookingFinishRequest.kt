package com.lockboxth.lockboxkiosk.http.model.personal

data class VerifyBookingFinishRequest(
    val generalprofile_id: Int,
    val txn: String,
    val verify_by: String,
    val screenshot: String
)
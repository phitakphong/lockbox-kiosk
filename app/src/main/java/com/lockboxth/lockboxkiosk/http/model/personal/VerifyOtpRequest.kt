package com.lockboxth.lockboxkiosk.http.model.personal

data class VerifyOtpRequest(
    val generalprofile_id: Int,
    val txn: String,
    val pin: String
)
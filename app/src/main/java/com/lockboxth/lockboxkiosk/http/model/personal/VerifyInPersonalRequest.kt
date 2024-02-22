package com.lockboxth.lockboxkiosk.http.model.personal

data class VerifyInPersonalRequest(
    val generalprofile_id: Int,
    val phone: String,
    val txn: String
)
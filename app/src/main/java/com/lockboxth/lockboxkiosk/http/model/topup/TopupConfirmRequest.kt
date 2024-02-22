package com.lockboxth.lockboxkiosk.http.model.topup

data class TopupConfirmRequest(
    val generalprofile_id: Int,
    val txn: String,
    val amount: Int
)
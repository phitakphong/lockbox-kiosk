package com.lockboxth.lockboxkiosk.http.model.topup

data class TopupCheckoutRequest(
    val txn: String,
    val generalprofile_id: Int,
    val payment_method: String,
    val screenshot: String
)

package com.lockboxth.lockboxkiosk.http.model.go

data class GoPaymentConfirmRequest(
    val generalprofile_id: Int,
    val txn: String,
    val payment_method: String,
    val promotion_code: String?
)


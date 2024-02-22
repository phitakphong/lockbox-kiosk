package com.lockboxth.lockboxkiosk.http.model.pudo

data class PudoReceiverPaymentConfirmRequest(
    val generalprofile_id: Int,
    val txn: String,
    val payment_method: String,
    val promotion_code: String?
)


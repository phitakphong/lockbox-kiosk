package com.lockboxth.lockboxkiosk.http.model.payment

data class PaymentMethodRequest(
    val generalprofile_id: Int,
    val txn: String
)


package com.lockboxth.lockboxkiosk.http.model.go

data class GoPaymentCancelRequest(
    val generalprofile_id: Int,
    val txn: String,
    val event: String
)


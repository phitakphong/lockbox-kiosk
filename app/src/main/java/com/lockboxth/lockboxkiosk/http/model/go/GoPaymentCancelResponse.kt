package com.lockboxth.lockboxkiosk.http.model.go

data class GoPaymentCancelResponse(
    val generalprofile_id: Int,
    val txn: String
)


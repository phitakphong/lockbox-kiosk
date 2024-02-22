package com.lockboxth.lockboxkiosk.http.model.kiosk

data class ClearTransactionRequest(
    val generalprofile_id: Int,
    val txn: String
)

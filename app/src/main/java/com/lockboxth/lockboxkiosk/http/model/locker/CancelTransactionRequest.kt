package com.lockboxth.lockboxkiosk.http.model.locker

data class CancelTransactionRequest(
    val generalprofile_id: Int,
    val txn: String,
    val event: String
)

package com.lockboxth.lockboxkiosk.http.model.pudo

data class PudoReceiverConfirmRequest(
    val generalprofile_id: Int,
    val txn: String,
    val screenshot: String
)


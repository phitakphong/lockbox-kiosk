package com.lockboxth.lockboxkiosk.http.model.go

data class GoReceiverVerifyRequest(
    val generalprofile_id: Int,
    val txn: String,
    val receiver_phone: String
)

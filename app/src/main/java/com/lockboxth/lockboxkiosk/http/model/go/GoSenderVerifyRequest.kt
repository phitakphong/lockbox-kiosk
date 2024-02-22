package com.lockboxth.lockboxkiosk.http.model.go

data class GoSenderVerifyRequest(
    val generalprofile_id: Int,
    val sender_phone: String,
    val lang: String
)

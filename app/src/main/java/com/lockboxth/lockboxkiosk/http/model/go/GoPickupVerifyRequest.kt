package com.lockboxth.lockboxkiosk.http.model.go

data class GoPickupVerifyRequest(
    val generalprofile_id: Int,
    val pin: String,
    val lang: String
)

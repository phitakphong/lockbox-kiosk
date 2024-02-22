package com.lockboxth.lockboxkiosk.http.model.pudo

data class PudoSenderVerifyRequest(
    val generalprofile_id: Int,
    val type: String,
    val data: String,
    val lang: String
)


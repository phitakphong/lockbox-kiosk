package com.lockboxth.lockboxkiosk.http.model.topup

data class TopupVerifyRequest(
    val generalprofile_id: Int,
    val phone: String
)
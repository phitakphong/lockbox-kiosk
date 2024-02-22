package com.lockboxth.lockboxkiosk.http.model.pudo

data class PudoConfirmRegisterMemberResponse(
    val phone: String,
    val otp_token: String
)

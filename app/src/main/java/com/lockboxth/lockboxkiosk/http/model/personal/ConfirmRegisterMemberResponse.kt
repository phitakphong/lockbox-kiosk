package com.lockboxth.lockboxkiosk.http.model.personal

data class ConfirmRegisterMemberResponse(
    val phone: String,
    val otp_token: String
)

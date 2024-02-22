package com.lockboxth.lockboxkiosk.http.model.personal

data class VerifyOtpResponse(
    val phone: String,
    val verify_type: String,
    val is_pending: Boolean,
    val has_pdpa: Boolean
)

package com.lockboxth.lockboxkiosk.http.model.personal

data class VerifyInPersonalResponse(
    val phone: String,
    val verify_type: String,
    val is_member: Boolean,
    val has_pdpa: Boolean
)

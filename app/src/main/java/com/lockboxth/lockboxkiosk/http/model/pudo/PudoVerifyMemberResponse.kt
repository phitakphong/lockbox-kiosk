package com.lockboxth.lockboxkiosk.http.model.pudo

data class PudoVerifyMemberResponse(
    val phone: String,
    val txn: String,
    val is_member: Boolean,
    val has_pdpa: Boolean
)

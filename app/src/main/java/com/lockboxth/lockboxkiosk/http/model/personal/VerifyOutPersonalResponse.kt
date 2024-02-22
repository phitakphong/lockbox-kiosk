package com.lockboxth.lockboxkiosk.http.model.personal

data class VerifyOutPersonalResponse(
    val txn: String,
    val phone: String,
    val verify_type: String
)

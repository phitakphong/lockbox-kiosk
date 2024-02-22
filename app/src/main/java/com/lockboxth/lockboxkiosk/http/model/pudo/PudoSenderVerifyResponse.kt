package com.lockboxth.lockboxkiosk.http.model.pudo

data class PudoSenderVerifyResponse(
    val txn: String,
    val booking_id: Int,
//    val verify_type: String,
    val has_pdpa: Boolean
)




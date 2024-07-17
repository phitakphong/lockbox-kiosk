package com.lockboxth.lockboxkiosk.http.model.personal

data class VerifyOutPersonalRequest(
    val generalprofile_id: Int,
    val phone: String,
    val out_type: String,
    val lang: String
)
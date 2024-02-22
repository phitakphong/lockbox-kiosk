package com.lockboxth.lockboxkiosk.http.model.cp

data class CpVerifyRequest(
    val generalprofile_id: Int,
    val type: String,
    val data: String,
    val lang: String
)

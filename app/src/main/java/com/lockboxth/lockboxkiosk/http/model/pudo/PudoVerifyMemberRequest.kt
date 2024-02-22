package com.lockboxth.lockboxkiosk.http.model.pudo

data class PudoVerifyMemberRequest(
    val generalprofile_id: Int,
    val phone: String,
    val lang: String
)


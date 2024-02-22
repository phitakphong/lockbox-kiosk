package com.lockboxth.lockboxkiosk.http.model.personal

data class ConfirmRegisterMemberRequest(
    val generalprofile_id: Int,
    val txn: String,
    val lang: String
)


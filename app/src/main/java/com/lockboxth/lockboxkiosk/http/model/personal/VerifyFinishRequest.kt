package com.lockboxth.lockboxkiosk.http.model.personal

data class VerifyFinishRequest(
    val generalprofile_id: Int,
    val txn: String,
    val verify_by: String
)
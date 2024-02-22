package com.lockboxth.lockboxkiosk.http.model.cp

data class CpReOpenRequest(
    val generalprofile_id: Int,
    val txn: String
)

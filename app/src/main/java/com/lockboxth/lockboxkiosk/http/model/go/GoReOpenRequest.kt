package com.lockboxth.lockboxkiosk.http.model.go

data class GoReOpenRequest(
    val generalprofile_id: Int,
    val txn: String
)

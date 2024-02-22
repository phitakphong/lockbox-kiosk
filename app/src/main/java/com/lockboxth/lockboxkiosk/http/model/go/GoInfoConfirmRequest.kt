package com.lockboxth.lockboxkiosk.http.model.go

data class GoInfoConfirmRequest(
    val generalprofile_id: Int,
    val txn: String,
)

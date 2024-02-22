package com.lockboxth.lockboxkiosk.http.model.locker

data class LockerCalculateRequest(
    val generalprofile_id: Int,
    val txn: String,
    val type: String,
    val recal: Boolean
)


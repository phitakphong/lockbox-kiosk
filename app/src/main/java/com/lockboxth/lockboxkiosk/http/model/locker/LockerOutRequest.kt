package com.lockboxth.lockboxkiosk.http.model.locker

data class LockerOutRequest(
    val generalprofile_id: Int,
    val txn: String
)


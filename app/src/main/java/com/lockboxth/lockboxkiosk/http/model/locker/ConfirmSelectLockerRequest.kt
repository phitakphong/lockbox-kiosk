package com.lockboxth.lockboxkiosk.http.model.locker

data class ConfirmSelectLockerRequest(
    val generalprofile_id: Int,
    val block_uses: ArrayList<Int>,
    val txn: String
)

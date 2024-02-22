package com.lockboxth.lockboxkiosk.http.model.cp

data class CpPickupAck(
    val generalprofile_id: Int,
    val txn: String,
    val submit_type: String
)

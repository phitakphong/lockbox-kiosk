package com.lockboxth.lockboxkiosk.http.model.locker

data class LockerCalculateDetailRequest(
    val generalprofile_id: Int,
    val txn: String,
    val type: String,
    val payment_method: String,
    val promotion_code: String?,

)

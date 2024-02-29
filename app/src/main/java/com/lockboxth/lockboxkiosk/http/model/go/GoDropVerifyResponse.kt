package com.lockboxth.lockboxkiosk.http.model.go

import com.lockboxth.lockboxkiosk.http.model.locker.LockerCalculateDetail

data class GoDropVerifyResponse(
    val exist_locker: Boolean,
    val sender_phone: String?,
    val receiver_phone: String?,
    val is_ap: Boolean?,
    val amount: Float?,
    val discount: Float?,
    val fee: Float?,
    val time_limit: Int?,
    val details: Map<String, LockerCalculateDetail>?
)

package com.lockboxth.lockboxkiosk.http.model.go

import com.lockboxth.lockboxkiosk.http.model.locker.LockerCalculateDetail
import com.lockboxth.lockboxkiosk.http.model.locker.LockerItem
import com.lockboxth.lockboxkiosk.http.model.locker.LockerSizeItem
import com.lockboxth.lockboxkiosk.http.model.locker.UseTransaction

data class GoLockerSelectResponse(
    val sender_phone: String,
    val receiver_phone: String,
    val is_ap: Boolean,
    val amount: Float,
    val discount: Float,
    val fee: Float,
    val time_limit: Int,
    val details: Map<String, LockerCalculateDetail>
)
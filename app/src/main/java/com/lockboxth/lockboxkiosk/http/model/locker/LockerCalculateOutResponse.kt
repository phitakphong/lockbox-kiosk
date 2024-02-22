package com.lockboxth.lockboxkiosk.http.model.locker

data class LockerCalculateOutResponse(
    val total_amount: Float,
    val amount_pay: Float,
    val already_paid: Float,
    val time_use_hour: Int,
    val time_use_min: Int,
    val time_over: Int,
    val details: Map<String, LockerCalculateDetail>

)


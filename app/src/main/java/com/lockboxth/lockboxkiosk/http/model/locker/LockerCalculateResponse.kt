package com.lockboxth.lockboxkiosk.http.model.locker

import android.os.Parcel
import android.os.Parcelable

data class LockerCalculateResponse(
    val amount: Float,
    val discount: Float,
    val fee: Float,
    val time_limit: Int,
    val details: Map<String, LockerCalculateDetail>
)


data class LockerCalculateDetail(
    val locker_no: List<String>,
    val rate_hour: Int,
    val rate_day: Int,
    val sum: Int,
)
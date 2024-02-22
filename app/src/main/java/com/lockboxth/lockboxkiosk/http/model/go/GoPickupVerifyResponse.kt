package com.lockboxth.lockboxkiosk.http.model.go

import android.os.Parcel
import android.os.Parcelable
import com.lockboxth.lockboxkiosk.http.model.locker.LockerCalculateDetail
import com.lockboxth.lockboxkiosk.http.model.locker.LockerItem
import com.lockboxth.lockboxkiosk.http.model.locker.LockerSizeItem
import com.lockboxth.lockboxkiosk.http.model.locker.UseTransaction

data class GoPickupVerifyResponse(
    val txn: String,
    val receiver_phone: String,
    val is_ap: Boolean,
    val fee: Float,
    val amount: Float,
    val discount: Float,
    val time_use_hour: Int,
    val time_use_min: Int,
    val time_use: Int,
    val time_over: Int,
    val details: Map<String, LockerCalculateDetail>
)
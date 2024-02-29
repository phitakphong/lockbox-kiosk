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
    val total_amount: Float,
    val time_limit: Int,
    val details: Map<String, GoPaymentConfirmDetail>
)
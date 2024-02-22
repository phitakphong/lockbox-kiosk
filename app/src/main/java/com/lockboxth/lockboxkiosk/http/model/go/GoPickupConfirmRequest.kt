package com.lockboxth.lockboxkiosk.http.model.go

import android.os.Parcel
import android.os.Parcelable
import com.lockboxth.lockboxkiosk.http.model.locker.LockerCalculateDetail
import com.lockboxth.lockboxkiosk.http.model.locker.LockerItem
import com.lockboxth.lockboxkiosk.http.model.locker.LockerSizeItem
import com.lockboxth.lockboxkiosk.http.model.locker.UseTransaction

data class GoPickupConfirmRequest(
    val generalprofile_id: Int,
    val txn: String,
    val screenshot: String
)
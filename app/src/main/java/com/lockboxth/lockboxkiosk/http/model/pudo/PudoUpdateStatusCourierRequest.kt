package com.lockboxth.lockboxkiosk.http.model.pudo

import android.os.Parcel
import android.os.Parcelable

data class PudoUpdateStatusCourierRequest(
    val generalprofile_id: Int,
    val txn: String,
    val order_id: Int,
    val status: String
)

